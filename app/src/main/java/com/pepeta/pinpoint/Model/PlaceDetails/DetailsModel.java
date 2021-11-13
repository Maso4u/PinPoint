package com.pepeta.pinpoint.Model.PlaceDetails;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pepeta.pinpoint.Model.NearByPlaces.GeometryModel;
import com.pepeta.pinpoint.Model.NearByPlaces.PhotoModel;
import com.pepeta.pinpoint.Model.NearByPlaces.PlusCodeModel;

public class DetailsModel implements Parcelable {

    //region Object Fields
    @SerializedName("address_components")
    @Expose
    private List<AddressComponentModel> addressComponents = null;
    @SerializedName("adr_address")
    @Expose
    private String adrAddress;
    @SerializedName("business_status")
    @Expose
    private String businessStatus;
    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;
    @SerializedName("geometry")
    @Expose
    private GeometryModel geometry;
    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("permanently_closed")
    @Expose
    private boolean permanentlyClosed;

    @SerializedName("opening_hours")
    @Expose
    private OpeningHoursModel openingHours;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("plus_code")
    @Expose
    private PlusCodeModel plusCode;
    @SerializedName("price_level")
    @Expose
    private Integer priceLevel;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("user_ratings_total")
    @Expose
    private Integer userRatingsTotal;
    @SerializedName("utc_offset")
    @Expose
    private Integer utcOffset;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("website")
    @Expose
    private String website;
    //endregion

    //region GETTERS AND SETTERS
    public List<AddressComponentModel> getAddressComponents() {
        return addressComponents;
    }

    public void setAddressComponents(List<AddressComponentModel> addressComponents) {
        this.addressComponents = addressComponents;
    }

    public String getAdrAddress() {
        return adrAddress;
    }

    public void setAdrAddress(String adrAddress) {
        this.adrAddress = adrAddress;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public void setFormattedPhoneNumber(String formattedPhoneNumber) {
        this.formattedPhoneNumber = formattedPhoneNumber;
    }

    public GeometryModel getGeometry() {
        return geometry;
    }

    public void setGeometry(GeometryModel geometry) {
        this.geometry = geometry;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isPermanentlyClosed() {
        return permanentlyClosed;
    }

    public void setPermanentlyClosed(boolean permanentlyClosed) {
        this.permanentlyClosed = permanentlyClosed;
    }

    public OpeningHoursModel getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHoursModel openingHours) {
        this.openingHours = openingHours;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Integer getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(Integer priceLevel) {
        this.priceLevel = priceLevel;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(Integer userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public Integer getUtcOffset() {
        return utcOffset;
    }

    public void setUtcOffset(Integer utcOffset) {
        this.utcOffset = utcOffset;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
    //endregion


    public DetailsModel() {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null || this.getClass()!=obj.getClass()){
            return false;
        }
        DetailsModel detailsModel =(DetailsModel) obj;
//        LatLng objLatLng = new LatLng(detailsModel.geometry.getLocation().getLat(),
//                detailsModel.geometry.getLocation().getLng());
//        LatLng thisLatLng = new LatLng(this.geometry.getLocation().getLat(),
//                this.geometry.getLocation().getLng());

        return this.getPlaceId().equals(detailsModel.getPlaceId());
    }

    //region PARCELABLE IMPLEMENTATION
    protected DetailsModel(Parcel in) {
        adrAddress = in.readString();
        businessStatus = in.readString();
        formattedAddress = in.readString();
        formattedPhoneNumber = in.readString();
        internationalPhoneNumber = in.readString();
        name = in.readString();
        placeId = in.readString();
        if (in.readByte() == 0) {
            priceLevel = null;
        } else {
            priceLevel = in.readInt();
        }
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readDouble();
        }
        reference = in.readString();
        types = in.createStringArrayList();
        url = in.readString();
        if (in.readByte() == 0) {
            userRatingsTotal = null;
        } else {
            userRatingsTotal = in.readInt();
        }
        if (in.readByte() == 0) {
            utcOffset = null;
        } else {
            utcOffset = in.readInt();
        }
        vicinity = in.readString();
        website = in.readString();
    }

    public static final Creator<DetailsModel> CREATOR = new Creator<DetailsModel>() {
        @Override
        public DetailsModel createFromParcel(Parcel in) {
            return new DetailsModel(in);
        }

        @Override
        public DetailsModel[] newArray(int size) {
            return new DetailsModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(adrAddress);
        dest.writeString(businessStatus);
        dest.writeString(formattedAddress);
        dest.writeString(formattedPhoneNumber);
        dest.writeString(internationalPhoneNumber);
        dest.writeString(name);
        dest.writeString(placeId);
        if (priceLevel == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(priceLevel);
        }
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(rating);
        }
        dest.writeString(reference);
        dest.writeStringList(types);
        dest.writeString(url);
        if (userRatingsTotal == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userRatingsTotal);
        }
        if (utcOffset == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(utcOffset);
        }
        dest.writeString(vicinity);
        dest.writeString(website);
    }
    //endregion
}