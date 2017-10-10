package org.kuali.ole.module.purap.aggresoInvoice;

import org.kuali.ole.deliver.bo.BatchJobLastRunBo;
import org.kuali.ole.docstore.common.util.DataSource;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by govindarajank on 5/9/17.
 */
public class InvoiceFileRecordProcessor{

    final StringBuilder record;
    private BusinessObjectService businessObjectService;

    public InvoiceFileRecordProcessor(int recordSize) {

        record = new StringBuilder(recordSize);
        record.setLength(recordSize);
    }

    public void setField(int pos, int length, String s) {
        record.replace(pos - 1, pos + length, String.format("%-" + length + "s",s));
    }

    public BusinessObjectService getBusinessObjectService() {
        if (null == businessObjectService) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

    public Timestamp getPreviousRunDate(){
        List<BatchJobLastRunBo> batchJobLastRunBoList = (List<BatchJobLastRunBo>) getBusinessObjectService().findAll(BatchJobLastRunBo.class);
        if(batchJobLastRunBoList != null) {
            BatchJobLastRunBo batchJobLastRunBo = batchJobLastRunBoList.get(0);
            return batchJobLastRunBo.getLastRunTIme();
        }
        return null;
    }

    public void savePreviousRunDate() {
        BatchJobLastRunBo batchJobLastRunBo;
        List<BatchJobLastRunBo> batchJobLastRunBoList = (List<BatchJobLastRunBo>) getBusinessObjectService().findAll(BatchJobLastRunBo.class);
        if (batchJobLastRunBoList != null) {
            batchJobLastRunBo = batchJobLastRunBoList.get(0);
            batchJobLastRunBo.setLastRunTIme(new Timestamp(new Date().getTime()));
        } else{
            batchJobLastRunBo = new BatchJobLastRunBo();
            batchJobLastRunBo.setId(1);
            batchJobLastRunBo.setLastRunTIme(new Timestamp(new Date().getTime()));
        }
        getBusinessObjectService().save(batchJobLastRunBo);
    }

    public List<String> getInvoiceDocumentIds(){
        String previousDate = "";
        Timestamp previousRunDateTime = getPreviousRunDate();
        if(previousRunDateTime != null){
            previousDate = previousRunDateTime.toString();
        }
        else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            previousDate = getFormatedDate(calendar.getTime());
        }
        List<String> invoiceIdList = new ArrayList<>();
        String currentDate = getFormatedDate(new Date());
        ResultSet invoiceIdResultSet=null;
        String query = "SELECT D.DOC_HDR_ID FROM KREW_DOC_HDR_T D,KREW_DOC_TYP_T T WHERE D.DOC_TYP_ID = T.DOC_TYP_ID AND D.DOC_HDR_STAT_CD = 'F' AND T.DOC_TYP_NM='OLE_PRQS' AND (D.CRTE_DT BETWEEN '"+previousDate+"' AND '"+currentDate+"')";
        try {
            PreparedStatement statement = getConnection().prepareStatement(query);
            invoiceIdResultSet = statement.executeQuery(query);
            while (invoiceIdResultSet.next()) {
                invoiceIdList.add(invoiceIdResultSet.getString(1));
            }
            statement.close();
            getConnection().close();
            return invoiceIdList;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getFormatedDate(Date lastExportDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(lastExportDate);
    }

    private Connection getConnection() throws SQLException {
        DataSource dataSource = null;
        try {
            dataSource = DataSource.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
           e.printStackTrace();
        } catch (PropertyVetoException e) {
           e.printStackTrace();
        }
        return dataSource.getConnection();
    }

    public String toString() {
        return record.toString();
    }
}
