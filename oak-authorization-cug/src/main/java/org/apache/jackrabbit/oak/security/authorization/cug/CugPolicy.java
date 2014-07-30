begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|authorization
operator|.
name|cug
package|;
end_package

begin_comment
comment|/*************************************************************************  *  * ADOBE CONFIDENTIAL  * ___________________  *  *  Copyright 2011 Adobe Systems Incorporated  *  All Rights Reserved.  *  * NOTICE:  All information contained herein is, and remains  * the property of Adobe Systems Incorporated and its suppliers,  * if any.  The intellectual and technical concepts contained  * herein are proprietary to Adobe Systems Incorporated and its  * suppliers and are protected by trade secret or copyright law.  * Dissemination of this information or reproduction of this material  * is strictly forbidden unless prior written permission is obtained  * from Adobe Systems Incorporated.  **************************************************************************/
end_comment

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|security
operator|.
name|JackrabbitAccessControlPolicy
import|;
end_import

begin_comment
comment|/**  * Denies read access for all principals except for the specified principals.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CugPolicy
extends|extends
name|JackrabbitAccessControlPolicy
block|{
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|getPrincipals
parameter_list|()
function_decl|;
name|boolean
name|addPrincipals
parameter_list|(
annotation|@
name|Nonnull
name|Principal
modifier|...
name|principals
parameter_list|)
throws|throws
name|AccessControlException
function_decl|;
name|boolean
name|removePrincipals
parameter_list|(
annotation|@
name|Nonnull
name|Principal
modifier|...
name|principals
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

