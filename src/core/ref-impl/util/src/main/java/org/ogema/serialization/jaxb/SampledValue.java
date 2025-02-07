/**
 * This file is part of OGEMA.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.09.13 at 08:01:54 AM CEST 
//
package org.ogema.serialization.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.Value;

/**
 * 
 * XML representation of an OGEMA SampledValue data type. This is the base class for more specialized SampledValues.
 * 
 * 
 * <p>
 * Java class for SampledValue complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SampledValue">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="time" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="quality" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SampledValue")
@XmlSeeAlso( { SampledTime.class, SampledOpaque.class, SampledFloat.class, SampledInteger.class, SampledString.class,
		SampledBoolean.class })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes( { @JsonSubTypes.Type(SampledBoolean.class), @JsonSubTypes.Type(SampledFloat.class),
		@JsonSubTypes.Type(SampledInteger.class), @JsonSubTypes.Type(SampledString.class),
		@JsonSubTypes.Type(SampledTime.class) })
public abstract class SampledValue {

	protected long time;
	protected Quality quality;

	// @XmlElement(required = true)
	// protected SampledValue value;
	/**
	 * Gets the value of the time property.
	 * 
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Sets the value of the time property.
	 * 
	 */
	public void setTime(long value) {
		this.time = value;
	}

	/**
	 * Gets the value of the quality property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public Quality getQuality() {
		return quality;
	}

	/**
	 * Sets the value of the quality property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setQuality(Quality value) {
		this.quality = value;
	}

	// /**
	// * Gets the value of the value property.
	// *
	// * @return
	// * possible object is
	// * {@link Object }
	// *
	// */
	// public Object getValue() {
	// return value;
	// }
	//
	// /**
	// * Sets the value of the value property.
	// *
	// * @param value
	// * allowed object is
	// * {@link Object }
	// *
	// */
	// public void setValue(Object value) {
	// this.value = value;
	// }
	public abstract void setValue(Value value);

	/** Creates the appropriate OGEMA value Object. */
	public abstract Value createOgemaValue();
}
