package gov.va.api.lighthouse.facilities.collector;

import gov.va.api.lighthouse.facilities.CmsOverlayRepository;
import lombok.NonNull;

public abstract class BaseCmsOverlayHandler {

  protected final CmsOverlayRepository cmsOverlayRepository;

  /** Default constructor for handling CMS overlay service data. */
  public BaseCmsOverlayHandler(@NonNull CmsOverlayRepository cmsOverlayRepository) {
    this.cmsOverlayRepository = cmsOverlayRepository;
  }
}
