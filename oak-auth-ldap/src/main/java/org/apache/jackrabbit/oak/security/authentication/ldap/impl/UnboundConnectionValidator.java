begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * **********************************************************************  *<p/>  * ADOBE CONFIDENTIAL  * ___________________  *<p/>  * Copyright ${today.year} Adobe Systems Incorporated  * All Rights Reserved.  *<p/>  * NOTICE:  All information contained herein is, and remains  * the property of Adobe Systems Incorporated and its suppliers,  * if any.  The intellectual and technical concepts contained  * herein are proprietary to Adobe Systems Incorporated and its  * suppliers and are protected by trade secret or copyright law.  * Dissemination of this information or reproduction of this material  * is strictly forbidden unless prior written permission is obtained  * from Adobe Systems Incorporated.  * ************************************************************************  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|security
operator|.
name|authentication
operator|.
name|ldap
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|ldap
operator|.
name|client
operator|.
name|api
operator|.
name|LdapConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|directory
operator|.
name|ldap
operator|.
name|client
operator|.
name|api
operator|.
name|LdapConnectionValidator
import|;
end_import

begin_comment
comment|/**  * {@code UnboundConnectionValidator}...  */
end_comment

begin_class
specifier|public
class|class
name|UnboundConnectionValidator
implements|implements
name|LdapConnectionValidator
block|{
comment|/**      * Returns true if<code>connection</code> is connected      *      * @param connection The connection to validate      * @return True, if the connection is still valid      */
annotation|@
name|Override
specifier|public
name|boolean
name|validate
parameter_list|(
name|LdapConnection
name|connection
parameter_list|)
block|{
return|return
name|connection
operator|.
name|isConnected
argument_list|()
return|;
block|}
block|}
end_class

end_unit

