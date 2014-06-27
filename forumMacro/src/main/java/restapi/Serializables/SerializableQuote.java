package restapi.Serializables;

import forummacro.forum.ao.QuoteAO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
public class SerializableQuote {

    private int ID;
    private String message;
    private Date quoteDate;
    private SerializableUser user;

    private SerializableQuote(int ID, String message, Date quoteDate, SerializableUser user) {
        this.ID = ID;
        this.message = message;
        this.quoteDate = quoteDate;
        this.user = user;
    }

    private SerializableQuote(){

    }

    public static SerializableQuote build(QuoteAO quoteAO) {
        if(quoteAO != null){
            return new SerializableQuote(quoteAO.getID(),quoteAO.getMessage(), quoteAO.getQuoteDate(), SerializableUserImplement.build(quoteAO.getUser()));
        }
        else{
            return null;
        }
    }

    @XmlElement
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @XmlElement
    public String getQuoteMessage() {
        return message;
    }

    public void setQuoteMessage(String message) {
        this.message = message;
    }


    @XmlElement
    public Date getQuoteDate() {   return quoteDate;  }

    public void setQuoteDate(Date quoteDate) {
        this.quoteDate = quoteDate;
    }


    @XmlElement
    public SerializableUser getUser() {   return user;  }

    public void setUser(SerializableUser user) {
        this.user = user;
    }

}
