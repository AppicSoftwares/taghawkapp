package com.taghawk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BlueSnapCardListResponse {

    @Expose
    @SerializedName("lastPaymentInfo")
    private LastPaymentInfo lastPaymentInfo;
    @Expose
    @SerializedName("paymentSources")
    private PaymentSources paymentSources;
    @Expose
    @SerializedName("shopperCurrency")
    private String shopperCurrency;
    @Expose
    @SerializedName("zip")
    private String zip;
    @Expose
    @SerializedName("lastName")
    private String lastName;
    @Expose
    @SerializedName("firstName")
    private String firstName;
    @Expose
    @SerializedName("vaultedShopperId")
    private int vaultedShopperId;

    public LastPaymentInfo getLastPaymentInfo() {
        return lastPaymentInfo;
    }

    public void setLastPaymentInfo(LastPaymentInfo lastPaymentInfo) {
        this.lastPaymentInfo = lastPaymentInfo;
    }

    public PaymentSources getPaymentSources() {
        return paymentSources;
    }

    public void setPaymentSources(PaymentSources paymentSources) {
        this.paymentSources = paymentSources;
    }

    public String getShopperCurrency() {
        return shopperCurrency;
    }

    public void setShopperCurrency(String shopperCurrency) {
        this.shopperCurrency = shopperCurrency;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
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

    public int getVaultedShopperId() {
        return vaultedShopperId;
    }

    public void setVaultedShopperId(int vaultedShopperId) {
        this.vaultedShopperId = vaultedShopperId;
    }

    public static class LastPaymentInfo {
        @Expose
        @SerializedName("creditCard")
        private CreditCardLastPayment creditCard;
        @Expose
        @SerializedName("paymentMethod")
        private String paymentMethod;

        public CreditCardLastPayment getCreditCard() {
            return creditCard;
        }

        public void setCreditCard(CreditCardLastPayment creditCard) {
            this.creditCard = creditCard;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
    }

    public static class CreditCardLastPayment {
        @Expose
        @SerializedName("cardType")
        private String cardType;
        @Expose
        @SerializedName("cardLastFourDigits")
        private String cardLastFourDigits;

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public String getCardLastFourDigits() {
            return cardLastFourDigits;
        }

        public void setCardLastFourDigits(String cardLastFourDigits) {
            this.cardLastFourDigits = cardLastFourDigits;
        }
    }

    public static class PaymentSources {
        @Expose
        @SerializedName("creditCardInfo")
        private List<CreditCardInfo> creditCardInfo;

        public List<CreditCardInfo> getCreditCardInfo() {
            return creditCardInfo;
        }

        public void setCreditCardInfo(List<CreditCardInfo> creditCardInfo) {
            this.creditCardInfo = creditCardInfo;
        }
    }

    public static class CreditCardInfo {
        @Expose
        @SerializedName("processingInfo")
        private ProcessingInfo processingInfo;
        @Expose
        @SerializedName("creditCard")
        private CreditCard creditCard;
        @Expose
        @SerializedName("billingContactInfo")
        private BillingContactInfo billingContactInfo;

        public ProcessingInfo getProcessingInfo() {
            return processingInfo;
        }

        public void setProcessingInfo(ProcessingInfo processingInfo) {
            this.processingInfo = processingInfo;
        }

        public CreditCard getCreditCard() {
            return creditCard;
        }

        public void setCreditCard(CreditCard creditCard) {
            this.creditCard = creditCard;
        }

        public BillingContactInfo getBillingContactInfo() {
            return billingContactInfo;
        }

        public void setBillingContactInfo(BillingContactInfo billingContactInfo) {
            this.billingContactInfo = billingContactInfo;
        }
    }

    public static class ProcessingInfo {
        @Expose
        @SerializedName("avsResponseCodeName")
        private String avsResponseCodeName;
        @Expose
        @SerializedName("avsResponseCodeAddress")
        private String avsResponseCodeAddress;
        @Expose
        @SerializedName("avsResponseCodeZip")
        private String avsResponseCodeZip;
        @Expose
        @SerializedName("cvvResponseCode")
        private String cvvResponseCode;

        public String getAvsResponseCodeName() {
            return avsResponseCodeName;
        }

        public void setAvsResponseCodeName(String avsResponseCodeName) {
            this.avsResponseCodeName = avsResponseCodeName;
        }

        public String getAvsResponseCodeAddress() {
            return avsResponseCodeAddress;
        }

        public void setAvsResponseCodeAddress(String avsResponseCodeAddress) {
            this.avsResponseCodeAddress = avsResponseCodeAddress;
        }

        public String getAvsResponseCodeZip() {
            return avsResponseCodeZip;
        }

        public void setAvsResponseCodeZip(String avsResponseCodeZip) {
            this.avsResponseCodeZip = avsResponseCodeZip;
        }

        public String getCvvResponseCode() {
            return cvvResponseCode;
        }

        public void setCvvResponseCode(String cvvResponseCode) {
            this.cvvResponseCode = cvvResponseCode;
        }
    }

    public static class CreditCard {
        @Expose
        @SerializedName("issuingCountryCode")
        private String issuingCountryCode;
        @Expose
        @SerializedName("expirationYear")
        private String expirationYear;
        @Expose
        @SerializedName("expirationMonth")
        private String expirationMonth;
        @Expose
        @SerializedName("issuingBank")
        private String issuingBank;
        @Expose
        @SerializedName("cardRegulated")
        private String cardRegulated;
        @Expose
        @SerializedName("binCategory")
        private String binCategory;
        @Expose
        @SerializedName("cardCategory")
        private String cardCategory;
        @Expose
        @SerializedName("cardSubType")
        private String cardSubType;
        @Expose
        @SerializedName("cardType")
        private String cardType;
        @Expose
        @SerializedName("cardLastFourDigits")
        private String cardLastFourDigits;

        public String getIssuingCountryCode() {
            return issuingCountryCode;
        }

        public void setIssuingCountryCode(String issuingCountryCode) {
            this.issuingCountryCode = issuingCountryCode;
        }

        public String getExpirationYear() {
            return expirationYear;
        }

        public void setExpirationYear(String expirationYear) {
            this.expirationYear = expirationYear;
        }

        public String getExpirationMonth() {
            return expirationMonth;
        }

        public void setExpirationMonth(String expirationMonth) {
            this.expirationMonth = expirationMonth;
        }

        public String getIssuingBank() {
            return issuingBank;
        }

        public void setIssuingBank(String issuingBank) {
            this.issuingBank = issuingBank;
        }

        public String getCardRegulated() {
            return cardRegulated;
        }

        public void setCardRegulated(String cardRegulated) {
            this.cardRegulated = cardRegulated;
        }

        public String getBinCategory() {
            return binCategory;
        }

        public void setBinCategory(String binCategory) {
            this.binCategory = binCategory;
        }

        public String getCardCategory() {
            return cardCategory;
        }

        public void setCardCategory(String cardCategory) {
            this.cardCategory = cardCategory;
        }

        public String getCardSubType() {
            return cardSubType;
        }

        public void setCardSubType(String cardSubType) {
            this.cardSubType = cardSubType;
        }

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public String getCardLastFourDigits() {
            return cardLastFourDigits;
        }

        public void setCardLastFourDigits(String cardLastFourDigits) {
            this.cardLastFourDigits = cardLastFourDigits;
        }
    }

    public static class BillingContactInfo {
        @Expose
        @SerializedName("zip")
        private String zip;
        @Expose
        @SerializedName("lastName")
        private String lastName;
        @Expose
        @SerializedName("firstName")
        private String firstName;

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
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
