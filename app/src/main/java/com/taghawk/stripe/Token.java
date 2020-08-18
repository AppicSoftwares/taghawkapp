
package com.taghawk.stripe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Token implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("object")
    @Expose
    private String object;
    @SerializedName("card")
    @Expose
    private Card card;
    @SerializedName("client_ip")
    @Expose
    private String clientIp;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("livemode")
    @Expose
    private Boolean livemode;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("used")
    @Expose
    private Boolean used;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public Boolean getLivemode() {
        return livemode;
    }

    public void setLivemode(Boolean livemode) {
        this.livemode = livemode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.object);
        dest.writeParcelable(this.card, flags);
        dest.writeString(this.clientIp);
        dest.writeValue(this.created);
        dest.writeValue(this.livemode);
        dest.writeString(this.type);
        dest.writeValue(this.used);
    }

    public Token() {
    }

    protected Token(Parcel in) {
        this.id = in.readString();
        this.object = in.readString();
        this.card = in.readParcelable(Card.class.getClassLoader());
        this.clientIp = in.readString();
        this.created = (Integer) in.readValue(Integer.class.getClassLoader());
        this.livemode = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.type = in.readString();
        this.used = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel source) {
            return new Token(source);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };
}
