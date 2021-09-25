package seol.study.upload.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import seol.study.upload.domain.Item;
import seol.study.upload.domain.ItemRepository;
import seol.study.upload.domain.UploadFile;
import seol.study.upload.file.FileStore;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemContorller {

	private final ItemRepository itemRepository;
	private final FileStore fileStore;


	@GetMapping("/items/new")
	public String newItem(@ModelAttribute ItemForm form) {
		return "item-form";
	}

	@PostMapping("/items/new")
	public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {
		// 파일 저장
		UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
		List<UploadFile> attachMultipleFiles = fileStore.storeFiles(form.getAttachMultipleFiles());

		// DB 저장
		Item item = new Item();
		item.setItemName(form.getItemName());
		item.setAttachFile(attachFile);
		item.setAttachMultipleFiles(attachMultipleFiles);
		itemRepository.save(item);

		redirectAttributes.addAttribute("itemId", item.getId());

		return "redirect:/items/{itemId}";
	}

	@GetMapping("/items/{id}")
	public String items(@PathVariable Long id, Model model) {
		Item item = itemRepository.findById(id);
		model.addAttribute("item", item);
		return "item-view";
	}

	@ResponseBody
	@GetMapping("/images/{filename}")
	public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
		return new UrlResource("file:" + fileStore.getFullPath(filename));
	}

	@GetMapping("/attach/{itemId}")
	public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException {
		Item item = itemRepository.findById(itemId);

		UploadFile attachFile = item.getAttachFile();
		String storeFileName = attachFile.getStoreFileName();
		String uploadFileName = attachFile.getUploadFileName();

		UrlResource resource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));
		log.info("uploadFileName = " + uploadFileName);

		String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
		String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
				.body(resource);
	}

}
