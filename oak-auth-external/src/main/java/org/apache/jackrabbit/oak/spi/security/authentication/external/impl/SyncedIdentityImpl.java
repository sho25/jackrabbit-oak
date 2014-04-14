begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
operator|.
name|impl
package|;
end_package

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
operator|.
name|ExternalIdentityRef
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|SyncedIdentity
import|;
end_import

begin_comment
comment|/** * {@code SyncedIdentityImpl}... */
end_comment

begin_class
specifier|public
class|class
name|SyncedIdentityImpl
implements|implements
name|SyncedIdentity
block|{
specifier|private
specifier|final
name|String
name|id
decl_stmt|;
specifier|private
specifier|final
name|ExternalIdentityRef
name|ref
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isGroup
decl_stmt|;
specifier|private
specifier|final
name|long
name|lastSynced
decl_stmt|;
specifier|public
name|SyncedIdentityImpl
parameter_list|(
name|String
name|id
parameter_list|,
name|ExternalIdentityRef
name|ref
parameter_list|,
name|boolean
name|isGroup
parameter_list|,
name|long
name|lastSynced
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
name|this
operator|.
name|isGroup
operator|=
name|isGroup
expr_stmt|;
name|this
operator|.
name|lastSynced
operator|=
name|lastSynced
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
specifier|public
name|ExternalIdentityRef
name|getExternalIdRef
parameter_list|()
block|{
return|return
name|ref
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isGroup
parameter_list|()
block|{
return|return
name|isGroup
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|lastSynced
parameter_list|()
block|{
return|return
name|lastSynced
return|;
block|}
block|}
end_class

end_unit

