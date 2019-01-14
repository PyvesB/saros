package saros.communication.extensions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.List;
import saros.negotiation.FileList;
import saros.negotiation.ProjectNegotiationData;
import saros.negotiation.TransferType;

@XStreamAlias(/* ProjectNegotiationOffering */ "PNOF")
public class ProjectNegotiationOfferingExtension extends ProjectNegotiationExtension {

  public static final Provider PROVIDER = new Provider();

  private List<ProjectNegotiationData> projectNegotiationData;
  private TransferType transferType;

  public ProjectNegotiationOfferingExtension(
      String sessionID,
      String negotiationID,
      List<ProjectNegotiationData> projectNegotiationData,
      TransferType transferType) {
    super(sessionID, negotiationID);
    this.projectNegotiationData = projectNegotiationData;
    this.transferType = transferType;
  }

  public List<ProjectNegotiationData> getProjectNegotiationData() {
    return projectNegotiationData;
  }

  public TransferType getTransferType() {
    return transferType;
  }

  public static class Provider
      extends ProjectNegotiationExtension.Provider<ProjectNegotiationOfferingExtension> {

    private Provider() {
      super(
          "pnof",
          ProjectNegotiationOfferingExtension.class,
          ProjectNegotiationData.class,
          TransferType.class,
          FileList.class);
    }
  }
}
