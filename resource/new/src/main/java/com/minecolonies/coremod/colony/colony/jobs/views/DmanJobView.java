package com.minecolonies.coremod.colony.colony.jobs.views;


/**
 * Extended dman job information on the client side, valid for all job types.
 */
public class DmanJobView extends DefaultJobView
{
    /**
     * The Token of the data store which belongs to this job.
     */
    private IToken<?> rsDataStoreToken;

    /**
     * Instantiate the dman job view.
     * @param iColonyView the colony it belongs to.
     * @param iCitizenDataView the citizen it belongs to.
     */
    public DmanJobView(final IColonyView iColonyView, final ICitizenDataView iCitizenDataView)
    {
        super(iColonyView, iCitizenDataView);
    }

    @Override
    public void deserialize(final PacketBuffer buffer)
    {
        super.deserialize(buffer);
        this.rsDataStoreToken = StandardFactoryController.getInstance().deserialize(buffer);
    }

    /**
     * Getter for the data store which belongs to this job.
     *
     * @return the crafter data store.
     */
    public IRequestSystemDeliveryManJobDataStore getDataStore()
    {
        return getColonyView()
                 .getRequestManager()
                 .getDataStoreManager()
                 .get(rsDataStoreToken, TypeConstants.REQUEST_SYSTEM_DELIVERY_MAN_JOB_DATA_STORE);
    }

    /**
     * Get the data store token assigned to it.
     * @return the token.
     */
    public IToken<?> getRsDataStoreToken()
    {
        return rsDataStoreToken;
    }
}
