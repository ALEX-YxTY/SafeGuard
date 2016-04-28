package com.exercise.yxty.safeguard.beans;

/**
 * Created by Administrator on 2016/2/17.
 */
public class ContactInfo {
    private String contactName;
    private String contactNumer;

    public ContactInfo() {
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumer() {
        return contactNumer;
    }

    public void setContactNumer(String contactNumer) {
        this.contactNumer = contactNumer;
    }

    @Override
    public String toString() {
        return "ContactInfo{" +
                "contactName='" + contactName + '\'' +
                ", contactNumer='" + contactNumer + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactInfo that = (ContactInfo) o;

        if (contactName != null ? !contactName.equals(that.contactName) : that.contactName != null)
            return false;
        return !(contactNumer != null ? !contactNumer.equals(that.contactNumer) : that.contactNumer != null);

    }

    @Override
    public int hashCode() {
        int result = contactName != null ? contactName.hashCode() : 0;
        result = 31 * result + (contactNumer != null ? contactNumer.hashCode() : 0);
        return result;
    }
}
