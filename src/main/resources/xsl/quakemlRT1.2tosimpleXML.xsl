<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:q="http://quakeml.org/xmlns/quakeml-rt/1.2" xmlns:qb="http://quakeml.org/xmlns/bed-rt/1.2">
    
<!--  
Copyright 2010, Institute of Geological & Nuclear Sciences Ltd or
third-party contributors as indicated by the @author tags.
 
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->
    
<!-- 
xslt template to transform quake events in quakeml-rt-1.2 xml document into simple event xml
 -->
    <xsl:output method="xml" encoding="UTF-8" indent="yes" standalone="yes"/>
   
    <xsl:template match="/q:quakeml">
      <xsl:apply-templates select="qb:eventParameters/qb:event"/>
      </xsl:template>

    <xsl:template match="qb:event">
        <xsl:variable name="preferredOriginID" select="qb:preferredOriginID"/>
        <xsl:variable name="preferredMagnitudeID" select="qb:preferredMagnitudeID"/>
        <xsl:variable name="publicID" select="@publicID"/>

        <xsl:element name="event">
            <xsl:element name="publicID">
                <xsl:value-of select="$publicID"/>
            </xsl:element>
            <xsl:element name="type">
                <xsl:value-of select="type"/>
            </xsl:element>
            <xsl:element name="agencyID">
                <xsl:value-of select="qb:creationInfo/qb:agencyID"/>
            </xsl:element>
            <xsl:apply-templates select="../qb:origin">
                <xsl:with-param name="preferredOriginID" select="$preferredOriginID"/>
                <xsl:with-param name="preferredMagnitudeID" select="$preferredMagnitudeID"/>
                <xsl:with-param name="publicID" select="$publicID"/>
            </xsl:apply-templates>

        </xsl:element>
    </xsl:template>

    <xsl:template match="qb:origin">
        <xsl:param name="preferredOriginID"/>
        <xsl:param name="preferredMagnitudeID"/>
        <xsl:param name="publicID"/>

           <xsl:if test="@publicID=$preferredOriginID">
            
                <xsl:element name="time">
                        <xsl:value-of select="qb:time/qb:value"/>
                </xsl:element>
                <xsl:element name="latitude">
                        <xsl:value-of select="qb:latitude/qb:value"/>
                </xsl:element>
                <xsl:element name="longitude">
                        <xsl:value-of select="qb:longitude/qb:value"/>
                </xsl:element>
                <xsl:element name="depth">
                        <xsl:value-of select="qb:depth/qb:value"/>
                </xsl:element>
                <xsl:apply-templates select="../qb:magnitude">
                    <xsl:with-param name="preferredMagnitudeID" select="$preferredMagnitudeID"/>
                </xsl:apply-templates>
               <xsl:element name="picks">
               <xsl:for-each select="qb:arrival">
                    <xsl:apply-templates select="../../qb:pick">
                        <xsl:with-param name="pickID" select="qb:pickID"/>
                        <xsl:with-param name="phase" select="qb:phase"/>
                        <xsl:with-param name="timeWeight" select="qb:timeWeight"/>
                    </xsl:apply-templates>
               </xsl:for-each>
               </xsl:element>
            
        </xsl:if>
    </xsl:template>
    
    
    <xsl:template match="qb:pick">
        <xsl:param name="pickID"/>
        <xsl:param name="phase"/>
        <xsl:param name="timeWeight"/>

        <xsl:if test="@publicID=$pickID">
           
            <xsl:element name="pick">
                <xsl:element name="phase">
                    <xsl:value-of select="$phase"/>
                </xsl:element>
                <xsl:element name="mode">
                    <xsl:value-of select="qb:evaluationMode"/>
                </xsl:element>
                <xsl:element name="status">
                    <xsl:value-of select="qb:evaluationStatus"/>
                </xsl:element>
                <xsl:element name="time">
                    <xsl:value-of select="qb:time/qb:value"/>
                </xsl:element>
                <xsl:element name="weight">
                    <xsl:value-of select="$timeWeight"/>
                </xsl:element>
                <xsl:element name="network">
                    <xsl:value-of select="qb:waveformID/@networkCode"/>
                </xsl:element>
                <xsl:element name="station">
                    <xsl:value-of select="qb:waveformID/@stationCode"/>
                </xsl:element>
                <xsl:element name="location">
                    <xsl:value-of select="qb:waveformID/@locationCode"/>
                </xsl:element>
                <xsl:element name="channel">
                    <xsl:value-of select="qb:waveformID/@channelCode"/>
                </xsl:element>
            </xsl:element>
            
        </xsl:if>
    </xsl:template>

    <xsl:template match="qb:magnitude">
        <xsl:param name="preferredMagnitudeID"/>
 
        <xsl:if test="@publicID=$preferredMagnitudeID">

                <xsl:element name="magnitude">
                        <xsl:value-of select="qb:mag/qb:value"/>
                </xsl:element>
                <xsl:element name="magnitudeType">
                    <xsl:value-of select="qb:type"/>
                </xsl:element>

            
        </xsl:if>
    </xsl:template>
  
 </xsl:stylesheet>
