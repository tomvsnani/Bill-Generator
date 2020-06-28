package Database;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@androidx.room.Entity
public class Entity {



    public static DiffUtil.ItemCallback<Entity> diff = new DiffUtil.ItemCallback<Entity>() {
        @Override
        public boolean areItemsTheSame(@NonNull Entity oldItem, @NonNull Entity newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Entity oldItem, @NonNull Entity newItem) {
            return oldItem.equals(newItem);
        }
    };
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("name")
    @Expose
    private String name="";
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber="";
    @SerializedName("houseNumber")
    @Expose
    private String houseNumber="";
    @SerializedName("account_created_on")
    @Expose
    private String accountCreatedOn="";
    @SerializedName("amountDue")
    @Expose
    private Integer amountDue=0;
    @SerializedName("paid_date")
    @Expose
    private String paidDate="";
    @SerializedName("amountPaid")
    @Expose
    private Integer amountPaid=0;
    @SerializedName("firebaseReferenceKey")
    @Expose
    private String firebaseReferenceKey="";
    @SerializedName("last_updated_on")
    @Expose
    private String lastUpdatedOn="";

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    @SerializedName("netPayablePrice")
    @Expose
    private Integer netPayablePrice;

    private Long user_id=0L;

    public Integer getInstallationAmount() {
        return installationAmount;
    }

    public void setInstallationAmount(Integer installationAmount) {
        this.installationAmount = installationAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    private Integer installationAmount=0;

    private String remarks="";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getAccountCreatedOn() {
        return accountCreatedOn;
    }

    public void setAccountCreatedOn(String accountCreatedOn) {
        this.accountCreatedOn = accountCreatedOn;
    }

    public Integer getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Integer amountDue) {
        this.amountDue = amountDue;
    }

    public String getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(String paidDate) {
        this.paidDate = paidDate;
    }

    public Integer getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Integer amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getFirebaseReferenceKey() {
        return firebaseReferenceKey;
    }

    public void setFirebaseReferenceKey(String firebaseReferenceKey) {
        this.firebaseReferenceKey = firebaseReferenceKey;
    }

    public String getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(String lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public Integer getNetPayablePrice() {
        return netPayablePrice;
    }

    public void setNetPayablePrice(Integer netPayablePrice) {
        this.netPayablePrice = netPayablePrice;
    }


    private Boolean equals(Entity entity) {
        return entity.getId().equals(this.getId()) && entity.getHouseNumber().equals(this.getHouseNumber())
              && entity.getAmountDue().equals(this.getAmountDue())
                && entity.getAmountPaid().equals(this.getAmountPaid())
                && entity.getPhoneNumber().equals(this.getPaidDate());
    }
}
