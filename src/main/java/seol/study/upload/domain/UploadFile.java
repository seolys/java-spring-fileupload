package seol.study.upload.domain;

import lombok.Data;

@Data
public class UploadFile {

	private String uploadFileName;
	private String storeFileName;

	public UploadFile(final String uploadFileName, final String storeFileName) {
		this.uploadFileName = uploadFileName;
		this.storeFileName = storeFileName;
	}
}
