package com.taghawk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PaymentStatusRequest implements Serializable {

    @Expose
    @SerializedName("transaction")
    private Transaction transaction;
    @Expose
    @SerializedName("ship_to")
    private Ship_to ship_to;
    @Expose
    @SerializedName("payment")
    private Payment payment;
    @Expose
    @SerializedName("products")
    private List<Products> products;
    @Expose
    @SerializedName("currency")
    private String currency = "";
    @Expose
    @SerializedName("amount")
    private double amount;
    @Expose
    @SerializedName("vaultedShopperId")
    private String vaultedShopperId;

    public String getVaultedShopperId() {
        return vaultedShopperId;
    }

    public void setVaultedShopperId(String vaultedShopperId) {
        this.vaultedShopperId = vaultedShopperId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public static class Transaction {
        @Expose
        @SerializedName("cardlastfourdigits")
        private String cardlastfourdigits;
        @Expose
        @SerializedName("zip")
        private String zip;
        @Expose
        @SerializedName("lastname")
        private String lastname;
        @Expose
        @SerializedName("firstname")
        private String firstname;
        @Expose
        @SerializedName("currency")
        private String currency;
        @Expose
        @SerializedName("amount")
        private String amount;
        @Expose
        @SerializedName("transactionid")
        private String transactionid;
        @Expose
        @SerializedName("shipping_choice")
        private String shipping_choice;
        @Expose
        @SerializedName("wallet")
        private String wallet;

        public String getCardlastfourdigits() {
            return cardlastfourdigits;
        }

        public void setCardlastfourdigits(String cardlastfourdigits) {
            this.cardlastfourdigits = cardlastfourdigits;
        }

        public String getZip() {
            return zip;
        }

        public void setZip(String zip) {
            this.zip = zip;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTransactionid() {
            return transactionid;
        }

        public void setTransactionid(String transactionid) {
            this.transactionid = transactionid;
        }

        public String getShipping_choice() {
            return shipping_choice;
        }

        public void setShipping_choice(String shipping_choice) {
            this.shipping_choice = shipping_choice;
        }

        public String getWallet() {
            return wallet;
        }

        public void setWallet(String wallet) {
            this.wallet = wallet;
        }
    }


    public Ship_to getShip_to() {
        return ship_to;
    }

    public void setShip_to(Ship_to ship_to) {
        this.ship_to = ship_to;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public static class Ship_to implements Serializable {
        @Expose
        @SerializedName("type")
        private String type = "";
        @Expose
        @SerializedName("phone")
        private String phone = "";
        @Expose
        @SerializedName("state")
        private String state = "";
        @Expose
        @SerializedName("street1")
        private String street1 = "";
        @Expose
        @SerializedName("postal_code")
        private String postal_code = "";
        @Expose
        @SerializedName("city")
        private String city = "";
        @Expose
        @SerializedName("email")
        private String email = "";
        @Expose
        @SerializedName("contact_name")
        private String contact_name = "";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStreet1() {
            return street1;
        }

        public void setStreet1(String street1) {
            this.street1 = street1;
        }

        public String getPostal_code() {
            return postal_code;
        }

        public void setPostal_code(String postal_code) {
            this.postal_code = postal_code;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getContact_name() {
            return contact_name;
        }

        public void setContact_name(String contact_name) {
            this.contact_name = contact_name;
        }
    }

    public static class Payment implements Serializable {
        @Expose
        @SerializedName("payment_method_details")
        private Payment_method_details payment_method_details;
        @Expose
        @SerializedName("billing_details")
        private Billing_details billing_details;
        @Expose
        @SerializedName("paid")
        private boolean paid;
        @Expose
        @SerializedName("balance_transaction")
        private String balance_transaction = "";
        @Expose
        @SerializedName("id")
        private String id = "";

        public Payment_method_details getPayment_method_details() {
            return payment_method_details;
        }

        public void setPayment_method_details(Payment_method_details payment_method_details) {
            this.payment_method_details = payment_method_details;
        }

        public Billing_details getBilling_details() {
            return billing_details;
        }

        public void setBilling_details(Billing_details billing_details) {
            this.billing_details = billing_details;
        }

        public boolean getPaid() {
            return paid;
        }

        public void setPaid(boolean paid) {
            this.paid = paid;
        }

        public String getBalance_transaction() {
            return balance_transaction;
        }

        public void setBalance_transaction(String balance_transaction) {
            this.balance_transaction = balance_transaction;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class Payment_method_details implements Serializable {
        @Expose
        @SerializedName("card")
        private Card card;
        @Expose
        @SerializedName("type")
        private String type = "";

        public Card getCard() {
            return card;
        }

        public void setCard(Card card) {
            this.card = card;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Card implements Serializable {
        @Expose
        @SerializedName("wallet")
        private Wallet wallet;
        @Expose
        @SerializedName("three_d_secure")
        private String three_d_secure = "";
        @Expose
        @SerializedName("network")
        private String network = "";
        @Expose
        @SerializedName("last4")
        private String last4 = "";
        @Expose
        @SerializedName("installments")
        private String installments = "";
        @Expose
        @SerializedName("funding")
        private String funding = "";
        @Expose
        @SerializedName("fingerprint")
        private String fingerprint = "";
        @Expose
        @SerializedName("exp_year")
        private int exp_year;
        @Expose
        @SerializedName("exp_month")
        private int exp_month;
        @Expose
        @SerializedName("country")
        private String country = "";
        @Expose
        @SerializedName("checks")
        private Checks checks;
        @Expose
        @SerializedName("brand")
        private String brand = "";

        public Wallet getWallet() {
            return wallet;
        }

        public void setWallet(Wallet wallet) {
            this.wallet = wallet;
        }

        public String getThree_d_secure() {
            return three_d_secure;
        }

        public void setThree_d_secure(String three_d_secure) {
            this.three_d_secure = three_d_secure;
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public String getLast4() {
            return last4;
        }

        public void setLast4(String last4) {
            this.last4 = last4;
        }

        public String getInstallments() {
            return installments;
        }

        public void setInstallments(String installments) {
            this.installments = installments;
        }

        public String getFunding() {
            return funding;
        }

        public void setFunding(String funding) {
            this.funding = funding;
        }

        public String getFingerprint() {
            return fingerprint;
        }

        public void setFingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
        }

        public int getExp_year() {
            return exp_year;
        }

        public void setExp_year(int exp_year) {
            this.exp_year = exp_year;
        }

        public int getExp_month() {
            return exp_month;
        }

        public void setExp_month(int exp_month) {
            this.exp_month = exp_month;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public Checks getChecks() {
            return checks;
        }

        public void setChecks(Checks checks) {
            this.checks = checks;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }
    }

    public static class Wallet implements Serializable {
        @Expose
        @SerializedName("type")
        private String type = "";
        @Expose
        @SerializedName("dynamic_last4")
        private String dynamic_last4 = "";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDynamic_last4() {
            return dynamic_last4;
        }

        public void setDynamic_last4(String dynamic_last4) {
            this.dynamic_last4 = dynamic_last4;
        }
    }

    public static class Checks implements Serializable {
        @Expose
        @SerializedName("cvc_check")
        private String cvc_check = "";
        @Expose
        @SerializedName("address_postal_code_check")
        private String address_postal_code_check = "";
        @Expose
        @SerializedName("address_line1_check")
        private String address_line1_check = "";

        public String getCvc_check() {
            return cvc_check;
        }

        public void setCvc_check(String cvc_check) {
            this.cvc_check = cvc_check;
        }

        public String getAddress_postal_code_check() {
            return address_postal_code_check;
        }

        public void setAddress_postal_code_check(String address_postal_code_check) {
            this.address_postal_code_check = address_postal_code_check;
        }

        public String getAddress_line1_check() {
            return address_line1_check;
        }

        public void setAddress_line1_check(String address_line1_check) {
            this.address_line1_check = address_line1_check;
        }
    }

    public static class Billing_details implements Serializable {
        @Expose
        @SerializedName("phone")
        private String phone = "";
        @Expose
        @SerializedName("name")
        private String name = "";
        @Expose
        @SerializedName("email")
        private String email = "";
        @Expose
        @SerializedName("address")
        private Address address;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public static class Address implements Serializable {
        @Expose
        @SerializedName("state")
        private String state = "";
        @Expose
        @SerializedName("postal_code")
        private String postal_code = "";
        @Expose
        @SerializedName("line2")
        private String line2 = "";
        @Expose
        @SerializedName("line1")
        private String line1 = "";
        @Expose
        @SerializedName("country")
        private String country = "";
        @Expose
        @SerializedName("city")
        private String city = "";

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPostal_code() {
            return postal_code;
        }

        public void setPostal_code(String postal_code) {
            this.postal_code = postal_code;
        }

        public String getLine2() {
            return line2;
        }

        public void setLine2(String line2) {
            this.line2 = line2;
        }

        public String getLine1() {
            return line1;
        }

        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    public static class Products implements Serializable {
        @Expose
        @SerializedName("sellerId")
        private String sellerId = "";
        @Expose
        @SerializedName("price")
        private String price = "";
        @Expose
        @SerializedName("productId")
        private String productId = "";
        @Expose
        @SerializedName("ownerId")
        private String ownerId;

        public String getSellerId() {
            return sellerId;
        }

        public void setSellerId(String sellerId) {
            this.sellerId = sellerId;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }
    }
}
