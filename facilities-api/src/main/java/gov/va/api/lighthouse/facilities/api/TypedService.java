package gov.va.api.lighthouse.facilities.api;

public interface TypedService extends ServiceType {
  public static final String INVALID_SVC_ID = "INVALID_ID";

  public TypeOfService serviceType();
}
