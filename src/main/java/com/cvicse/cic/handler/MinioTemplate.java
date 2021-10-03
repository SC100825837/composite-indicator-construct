package com.cvicse.cic.handler;

import com.cvicse.cic.util.exception.BusinessException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

/**
 * minio 交互类
 *
 * @author SunChao
 *
 * <p>https://docs.min.io/docs/java-client-api-reference.html</p>
 */
@RequiredArgsConstructor
public class MinioTemplate implements InitializingBean {
    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private MinioClient minioClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(endpoint, "Minio url 为空");
        Assert.hasText(accessKey, "Minio accessKey为空");
        Assert.hasText(secretKey, "Minio secretKey为空");
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 判断bucket是否存在
     *
     * @param bucketName
     * @return
     */
    public boolean bucketExists(String bucketName) throws Exception {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
        return minioClient.bucketExists(bucketExistsArgs);
    }

    /**
     * 获取全部bucket
     */
    @SneakyThrows
    public List<Bucket> getAllBuckets() {
        return minioClient.listBuckets();
    }

    /**
     * Lists objects information.
     *
     * @param bucketName
     * @return
     */
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {
        return minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 根据文件前缀和后缀查询文件
     *
     * @param bucketName
     * @param prefix
     * @param startAfter
     * @return
     */
    @SneakyThrows
    public Iterable<Result<Item>> getAllObjectsByPrefix(String bucketName, String prefix, String startAfter) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .startAfter(startAfter)
                        .prefix(prefix)
                        .maxKeys(100)
                        .build());
    }

    /**
     * 创建bucket
     * Create bucket with default region.
     *
     * @param bucketName bucket名称
     */
    public void createBucket(String bucketName) throws Exception {
        if (!bucketExists(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        }
    }

    /**
     * @param bucketName bucket名称
     */
    @SneakyThrows
    public void removeBucket(String bucketName) {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * @param bucketName bucket名称
     */
    @SneakyThrows
    public Optional<Bucket> getBucket(String bucketName) {
        return getAllBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
    }

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     */
    public InputStream getObject(String bucketName, String objectName) throws IOException, GeneralSecurityException, MinioException, ReflectiveOperationException {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

    /**
     * 下载文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param fileName   下载的文件名称
     */
    @SneakyThrows
    public void downloadObject(String bucketName, String objectName, String fileName) {
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .filename(fileName)
                        .build());
    }

    /**
     * Upload known sized input stream.
     *
     * @param bucketName
     * @param objectName
     * @param inputStream
     * @param streamSize
     * @return
     */
    @SneakyThrows
    public ObjectWriteResponse putObject(String bucketName, String objectName, InputStream inputStream, long streamSize) {
        return minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, streamSize, -1)
                .build());
    }

    /**
     * Upload unknown sized input stream.
     *
     * @param bucketName
     * @param objectName
     * @param inputStream
     * @return
     */
    public ObjectWriteResponse putObject(String bucketName, String objectName, InputStream inputStream) throws IOException, MinioException, GeneralSecurityException {
        return minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, -1, 10485760)
                .build());
    }

    /**
     * 上传对象
     *
     * @param bucketName
     * @param objectName
     * @param fileName
     */
    public void uploadObject(String bucketName, String objectName, String fileName) throws IOException, MinioException, GeneralSecurityException {
        // Upload an JSON file.
        minioClient.uploadObject(UploadObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .filename(fileName)
                .build());
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#removeObject
     */
    public void removeObject(String bucketName, String objectName) throws Exception {
        // Remove object.
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

    /**
     * 获取文件信息
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#statObject
     */
    public StatObjectResponse getObjectInfo(String bucketName, String objectName) throws Exception {
        // Get information of an object.
        return minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

}
