package com.cvicse.cic.util;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 */
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ResultData<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private int code;

	@Getter
	@Setter
	private String msg;


	@Getter
	@Setter
	private T data;

	public static <T> ResultData<T> success() {
		return restResult(null, ReturnCode.RC200.getCode(), null);
	}

	public static <T> ResultData<T> success(T data) {
		return restResult(data, ReturnCode.RC200.getCode(), null);
	}

	public static <T> ResultData<T> success(T data, String msg) {
		return restResult(data, ReturnCode.RC200.getCode(), msg);
	}

	public static <T> ResultData<T> fail() {
		return restResult(null, ReturnCode.RC500.getCode(), ReturnCode.RC500.getMessage());
	}

	public static <T> ResultData<T> fail(String msg) {
		return restResult(null, ReturnCode.RC500.getCode(), msg);
	}

	public static <T> ResultData<T> fail(T data) {
		return restResult(data, ReturnCode.RC500.getCode(), null);
	}

	public static <T> ResultData<T> fail(T data, String msg) {
		return restResult(data, ReturnCode.RC500.getCode(), msg);
	}

	public static <T> ResultData<T> fail(int code, String msg) {
		return restResult(null, ReturnCode.RC500.getCode(), msg);
	}

	private static <T> ResultData<T> restResult(T data, int code, String msg) {
		ResultData<T> apiResult = new ResultData<>();
		apiResult.setCode(code);
		apiResult.setData(data);
		apiResult.setMsg(msg);
		return apiResult;
	}
}
