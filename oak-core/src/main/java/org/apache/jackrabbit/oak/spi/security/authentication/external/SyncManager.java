begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*************************************************************************  *  * ADOBE CONFIDENTIAL  * ___________________  *  *  Copyright ${today.year} Adobe Systems Incorporated  *  All Rights Reserved.  *  * NOTICE:  All information contained herein is, and remains  * the property of Adobe Systems Incorporated and its suppliers,  * if any.  The intellectual and technical concepts contained  * herein are proprietary to Adobe Systems Incorporated and its  * suppliers and are protected by trade secret or copyright law.  * Dissemination of this information or reproduction of this material  * is strictly forbidden unless prior written permission is obtained  * from Adobe Systems Incorporated.  **************************************************************************/
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * The external identity synchronization management.  *  * The default manager is registered as OSGi service and can also be retrieved via  * {@link org.apache.jackrabbit.oak.spi.security.SecurityProvider#getConfiguration(Class)}  */
end_comment

begin_interface
specifier|public
interface|interface
name|SyncManager
block|{
comment|/**      * Returns the sync handler with the given name.      * @param name the name of the sync handler or {@code null}      * @return the sync handler      */
annotation|@
name|CheckForNull
name|SyncHandler
name|getSyncHandler
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

