package org.craftercms.social.domain;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

public class AttachmentsList {

	private static final List<String> IMAGE_CONTENT_TYPE = new ArrayList<String>() {
		{
			add("image/g3fax");
			add("image/gif");
			add("image/png");
			add("image/ief");
			add("image/jpeg");
			add("image/tiff");
		}
	};
	private static final List<String> VIDEO_CONTENT_TYPE = new ArrayList<String>() {
		{
			add("video/webm");
			add("video/ogg");
		}
	};

	private List<AttachmentModel> imageAttach;
	private List<AttachmentModel> regularAttach;
	private List<AttachmentModel> videoAttach;

	public AttachmentsList() {
		imageAttach = new ArrayList<AttachmentModel>();
		regularAttach = new ArrayList<AttachmentModel>();
		videoAttach = new ArrayList<AttachmentModel>();
	}

	public void addAttachmentModel(AttachmentModel attachmentModel) {
		if (isImage(attachmentModel.getContentType())) {
			imageAttach.add(attachmentModel);
		} else if (isVideo(attachmentModel.getContentType())) {
			videoAttach.add(attachmentModel);
		} else {
			regularAttach.add(attachmentModel);
		}
	}

	public List<AttachmentModel> getImageAttach() {
		return imageAttach;
	}

	public List<AttachmentModel> getRegularAttach() {
		return regularAttach;
	}

	public List<AttachmentModel> getVideoAttach() {
		return videoAttach;
	}
	
	public ObjectId findObjectId(MultipartFile file) {
		List<AttachmentModel> currentList = regularAttach;
		if (isImage(file.getContentType())) {
			currentList = imageAttach;
		} else if (isVideo(file.getContentType())) {
			currentList = videoAttach;
		}
		ObjectId attachmentId = null;
		for (AttachmentModel model: currentList) {
          if (model.getFilename().equals(file.getName())) {
              attachmentId = new ObjectId(model.getAttachmentId());
              break;
          }
		}
		return attachmentId;
	}

	private boolean isVideo(String contentType) {
		return VIDEO_CONTENT_TYPE.contains(contentType);
	}

	private boolean isImage(String contentType) {
		return IMAGE_CONTENT_TYPE.contains(contentType);
	}

}
