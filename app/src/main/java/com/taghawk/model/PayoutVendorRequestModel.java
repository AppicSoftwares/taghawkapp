package com.taghawk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PayoutVendorRequestModel {

    @Expose
    @SerializedName("vendorAgreement")
    private VendorAgreement vendorAgreement;
    @Expose
    @SerializedName("payoutInfo")
    private List<PayoutInfo> payoutInfo;
    @Expose
    @SerializedName("vendorPrincipal")
    private VendorPrincipal vendorPrincipal;
    @Expose
    @SerializedName("defaultPayoutCurrency")
    private String defaultPayoutCurrency;
    @Expose
    @SerializedName("vendorUrl")
    private String vendorUrl;
    @Expose
    @SerializedName("state")
    private String state;
    @Expose
    @SerializedName("phone")
    private String phone;
    @Expose
    @SerializedName("country")
    private String country;
    @Expose
    @SerializedName("zip")
    private String zip;
    @Expose
    @SerializedName("city")
    private String city;
    @Expose
    @SerializedName("address")
    private String address;
    @Expose
    @SerializedName("lastName")
    private String lastName;
    @Expose
    @SerializedName("firstName")
    private String firstName;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("email")
    private String email;

    public VendorAgreement getVendorAgreement() {
        return vendorAgreement;
    }

    public void setVendorAgreement(VendorAgreement vendorAgreement) {
        this.vendorAgreement = vendorAgreement;
    }

    public List<PayoutInfo> getPayoutInfo() {
        return payoutInfo;
    }

    public void setPayoutInfo(List<PayoutInfo> payoutInfo) {
        this.payoutInfo = payoutInfo;
    }

    public VendorPrincipal getVendorPrincipal() {
        return vendorPrincipal;
    }

    public void setVendorPrincipal(VendorPrincipal vendorPrincipal) {
        this.vendorPrincipal = vendorPrincipal;
    }

    public String getDefaultPayoutCurrency() {
        return defaultPayoutCurrency;
    }

    public void setDefaultPayoutCurrency(String defaultPayoutCurrency) {
        this.defaultPayoutCurrency = defaultPayoutCurrency;
    }

    public String getVendorUrl() {
        return vendorUrl;
    }

    public void setVendorUrl(String vendorUrl) {
        this.vendorUrl = vendorUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static class VendorAgreement {
        @Expose
        @SerializedName("accountStatus")
        private String accountStatus;
        @Expose
        @SerializedName("commissionPercent")
        private int commissionPercent;

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public int getCommissionPercent() {
            return commissionPercent;
        }

        public void setCommissionPercent(int commissionPercent) {
            this.commissionPercent = commissionPercent;
        }
    }

    public static class PayoutInfo {
        @Expose
        @SerializedName("bankAccountId")
        private String bankAccountId;
        @Expose
        @SerializedName("zip")
        private String zip;
        @Expose
        @SerializedName("state")
        private String state;
        @Expose
        @SerializedName("address")
        private String address;
        @Expose
        @SerializedName("city")
        private String city;
        @Expose
        @SerializedName("country")
        private String country;
        @Expose
        @SerializedName("bankId")
        private String bankId;
        @Expose
        @SerializedName("bankName")
        private String bankName;
        @Expose
        @SerializedName("bankAccountType")
        private String bankAccountType;
        @Expose
        @SerializedName("bankAccountClass")
        private String bankAccountClass;
        @Expose
        @SerializedName("nameOnAccount")
        private String nameOnAccount;
        @Expose
        @SerializedName("baseCurrency")
        private String baseCurrency;
        @Expose
        @SerializedName("payoutType")
        private String payoutType;
        @Expose
        @SerializedName("minimalPayoutAmount")
        private int minimalPayoutAmount;

        public int getMinimalPayoutAmount() {
            return minimalPayoutAmount;
        }

        public void setMinimalPayoutAmount(int minimalPayoutAmount) {
            this.minimalPayoutAmount = minimalPayoutAmount;
        }

        public String getBankAccountId() {
            return bankAccountId;
        }

        public void setBankAccountId(String bankAccountId) {
            this.bankAccountId = bankAccountId;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getBankId() {
            return bankId;
        }

        public void setBankId(String bankId) {
            this.bankId = bankId;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getBankAccountType() {
            return bankAccountType;
        }

        public void setBankAccountType(String bankAccountType) {
            this.bankAccountType = bankAccountType;
        }

        public String getBankAccountClass() {
            return bankAccountClass;
        }

        public void setBankAccountClass(String bankAccountClass) {
            this.bankAccountClass = bankAccountClass;
        }

        public String getNameOnAccount() {
            return nameOnAccount;
        }

        public void setNameOnAccount(String nameOnAccount) {
            this.nameOnAccount = nameOnAccount;
        }

        public String getBaseCurrency() {
            return baseCurrency;
        }

        public void setBaseCurrency(String baseCurrency) {
            this.baseCurrency = baseCurrency;
        }

        public String getPayoutType() {
            return payoutType;
        }

        public void setPayoutType(String payoutType) {
            this.payoutType = payoutType;
        }
    }

    public static class VendorPrincipal {
        @Expose
        @SerializedName("email")
        private String email;
        @Expose
        @SerializedName("driverLicenseNumber")
        private String driverLicenseNumber;
        @Expose
        @SerializedName("personalIdentificationNumber")
        private int personalIdentificationNumber;
        @Expose
        @SerializedName("passportNumber")
        private String passportNumber;
        @Expose
        @SerializedName("dob")
        private String dob;
        @Expose
        @SerializedName("country")
        private String country;
        @Expose
        @SerializedName("zip")
        private String zip;
        @Expose
        @SerializedName("city")
        private String city;
        @Expose
        @SerializedName("address")
        private String address;
        @Expose
        @SerializedName("lastName")
        private String lastName;
        @Expose
        @SerializedName("firstName")
        private String firstName;

        public String getPassportNumber() {
            return passportNumber;
        }

        public void setPassportNumber(String passportNumber) {
            this.passportNumber = passportNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDriverLicenseNumber() {
            return driverLicenseNumber;
        }

        public void setDriverLicenseNumber(String driverLicenseNumber) {
            this.driverLicenseNumber = driverLicenseNumber;
        }

        public int getPersonalIdentificationNumber() {
            return personalIdentificationNumber;
        }

        public void setPersonalIdentificationNumber(int personalIdentificationNumber) {
            this.personalIdentificationNumber = personalIdentificationNumber;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
    }
}
