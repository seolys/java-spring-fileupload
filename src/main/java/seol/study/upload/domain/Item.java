package seol.study.upload.domain;

import java.util.List;
import lombok.Data;

@Data
public class Item {

	private Long id;
	private String itemName;
	private UploadFile attachFile;
	private List<UploadFile> attachMultipleFiles;

}
