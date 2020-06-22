package Database;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.PrimaryKey;

@androidx.room.Entity
public class Entity {
    public boolean isExcessAmountPaid() {
        return isExcessAmountPaid;
    }

    public void setExcessAmountPaid(boolean excessAmountPaid) {
        isExcessAmountPaid = excessAmountPaid;
    }

    private String last_updated_on="";
    private boolean isExcessAmountPaid=false;

    public String getLast_updated_on() {
        return last_updated_on;
    }

    public void setLast_updated_on(String last_updated_on) {
        this.last_updated_on = last_updated_on;
    }

    public static DiffUtil.ItemCallback<Entity> diff = new DiffUtil.ItemCallback<Entity>() {
        @Override
        public boolean areItemsTheSame(@NonNull Entity oldItem, @NonNull Entity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Entity oldItem, @NonNull Entity newItem) {
            return oldItem.equals(newItem);
        }
    };
    private String HouseNumber="";
    private String Name="";
    private int AmountPaid=0;
    private int AmountDue=0;
    private String paid_date="";
    private Long phone_number=0L;
    private String account_created_on;

    public String getFirebaseReferenceKey() {
        return FirebaseReferenceKey;
    }

    public void setFirebaseReferenceKey(String firebaseReferenceKey) {
        FirebaseReferenceKey = firebaseReferenceKey;
    }

    private String FirebaseReferenceKey;

    public String getAccount_created_on() {
        return account_created_on;
    }

    public void setAccount_created_on(String account_created_on) {
        this.account_created_on = account_created_on;
    }

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private int paying_delayed_by_days;
    private int netPayablePrice=600;

    public int getNetPayablePrice() {
        return netPayablePrice;
    }

    public void setNetPayablePrice(int netPayablePrice) {
        this.netPayablePrice = netPayablePrice;
    }

    public int getPaying_delayed_by_days() {
        return paying_delayed_by_days;
    }

    public void setPaying_delayed_by_days(int paying_delayed_by_days) {
        this.paying_delayed_by_days = paying_delayed_by_days;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHouseNumber() {
        return HouseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        HouseNumber = houseNumber;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getAmountPaid() {
        return AmountPaid;
    }

    public void setAmountPaid(int amountPaid) {
        AmountPaid = amountPaid;
    }

    public int getAmountDue() {
        return AmountDue;
    }

    public void setAmountDue(int amountDue) {
        AmountDue = amountDue;
    }

    public String getPaid_date() {
        return paid_date;
    }

    public void setPaid_date(String paid_date) {
        this.paid_date = paid_date;
    }

    public Long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(Long phone_number) {
        this.phone_number = phone_number;
    }


    private Boolean equals(Entity entity) {
        return entity.getId() == this.getId() && entity.getHouseNumber().equals(this.getHouseNumber())
                && entity.getPhone_number().equals(this.getPhone_number()) && entity.getAmountDue()==this.getAmountDue()
                && entity.getAmountPaid()==this.getAmountPaid();
    }
}
