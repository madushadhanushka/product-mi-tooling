<localEntry key="response_transform.xslt">
    <xsl:stylesheet version="2.0"
                    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                    xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
                    xmlns:m0="http://services.samples"
                    xmlns:ax21="http://services.samples/xsd"
                    exclude-result-prefixes="m0 ax21 fn">
        <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

        <xsl:template match="@* | node()">
            <xsl:copy>
                <xsl:apply-templates select="@* | node()"/>
            </xsl:copy>
        </xsl:template>

        <xsl:template match="m0:getQuoteResponse">

            <m:CheckPriceResponse xmlns:m="http://services.samples/xsd">
                <m:Code>
                    <xsl:value-of select="m0:return/ax21:symbol"/>
                </m:Code>
                <m:Price>
                    <xsl:value-of select="m0:return/ax21:last"/>
                </m:Price>
            </m:CheckPriceResponse>

        </xsl:template>

    </xsl:stylesheet>
</localEntry>