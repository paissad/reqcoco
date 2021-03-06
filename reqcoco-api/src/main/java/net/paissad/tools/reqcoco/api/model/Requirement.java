package net.paissad.tools.reqcoco.api.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "requirement")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class Requirement implements Serializable, Comparator<Requirement>, Comparable<Requirement> {

    private static final long  serialVersionUID = 1L;

    public static final String VERSION_UNKNOWN  = "N/A";

    public static final Status DEFAULT_STATUS   = Status.TODO;

    @XmlAttribute(required = true)
    @XmlID
    @Expose
    private String             name;

    @XmlElement
    @Expose
    private String             group;

    @XmlAttribute
    @Expose
    private boolean            ignore;

    @XmlElement(required = true)
    @Expose
    private String             shortDescription;

    @XmlElement
    @Expose
    private String             fullDescription;

    @XmlAttribute(required = true)
    @Expose
    private String             version;

    @XmlAttribute(required = true)
    private String             revision;

    @XmlElement(required = true)
    @Expose
    private Status             codeStatus;

    @XmlElement
    @Expose
    private String             codeAuthor;

    @XmlElement
    @Expose
    private String             codeAuthorComment;

    @XmlElement(required = true)
    @Expose
    private Status             testStatus;

    @XmlElement
    @Expose
    private String             testAuthor;

    @XmlElement
    @Expose
    private String             testAuthorComment;

    @XmlElement
    @Expose
    private String             link;

    public Requirement() {
        // default no-arg constructor
    }

    public Requirement(String id, String version, String revision) {
        this.name = id;
        this.version = version;
        this.revision = revision;
    }
    
    public boolean isCodeDone() {
        return Status.DONE == this.getCodeStatus();
    }
    
    public boolean isTestDone() {
        return Status.DONE == this.getTestStatus();
    }
    
    public boolean isCodeIgnore() {
        return Status.IGNORE == this.getCodeStatus() || isIgnore();
    }
    
    public boolean isTestIgnore() {
        return Status.IGNORE == this.getTestStatus() || isIgnore();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Requirement [name=");
        builder.append(name);
        builder.append(", group=");
        builder.append(group);
        builder.append(", ignore=");
        builder.append(ignore);
        builder.append(", shortDescription=");
        builder.append(shortDescription);
        builder.append(", version=");
        builder.append(version);
        builder.append(", revision=");
        builder.append(revision);
        builder.append(", codeStatus=");
        builder.append(codeStatus);
        builder.append(", codeAuthor=");
        builder.append(codeAuthor);
        builder.append(", testStatus=");
        builder.append(testStatus);
        builder.append(", testAuthor=");
        builder.append(testAuthor);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result + ((revision == null) ? 0 : revision.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Requirement))
            return false;
        Requirement other = (Requirement) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        if (revision == null) {
            if (other.revision != null)
                return false;
        } else if (!revision.equals(other.revision))
            return false;
        return true;
    }

    @Override
    public int compareTo(final Requirement other) {
        return compare(this, other);
    }

    @Override
    public int compare(final Requirement req1, final Requirement req2) {
        int compare = StringUtils.compare(req1.getName(), req2.getName());
        if (compare == 0) {
            compare = StringUtils.compare(req1.getVersion(), req2.getVersion());
            if (compare == 0) {
                return StringUtils.compare(req1.getRevision(), req2.getRevision());
            }
        }
        return compare;
    }

}
