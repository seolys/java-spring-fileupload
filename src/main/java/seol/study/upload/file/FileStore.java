package seol.study.upload.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import seol.study.upload.domain.UploadFile;

@Component
public class FileStore {

	@Value("${file.dir}")
	private String fileDir;

	public String getFullPath(String filename) {
		return fileDir + filename;
	}

	public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
		ArrayList<UploadFile> storeFileResult = new ArrayList<>();
		for (final MultipartFile multipartFile : multipartFiles) {
			if (multipartFile.isEmpty()) {
				continue;
			}
			UploadFile uploadFile = storeFile(multipartFile);
			storeFileResult.add(uploadFile);
		}
		return storeFileResult;
	}

	public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
		if (multipartFile.isEmpty()) {
			return null;
		}

		String originalFileName = multipartFile.getOriginalFilename();
		String storeFileName = createStoreFileName(originalFileName); // 서버에 저장하는 파일명
		multipartFile.transferTo(new File(getFullPath(storeFileName)));

		return new UploadFile(originalFileName, storeFileName);
	}

	private String createStoreFileName(final String originalFilename) {
		String ext = extractExt(originalFilename);
		return UUID.randomUUID().toString() + "." + ext;
	}

	private String extractExt(final String originalFileName) {
		int pos = originalFileName.lastIndexOf(".");
		return originalFileName.substring(pos + 1);
	}

}
