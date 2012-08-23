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
name|security
operator|.
name|authentication
package|;
end_package

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|AuthInfo
import|;
end_import

begin_comment
comment|/**  * AuthInfoImpl... TODO  */
end_comment

begin_class
specifier|public
class|class
name|AuthInfoImpl
implements|implements
name|AuthInfo
block|{
specifier|private
specifier|final
name|String
name|userID
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributes
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
decl_stmt|;
specifier|public
name|AuthInfoImpl
parameter_list|(
name|String
name|userID
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributes
parameter_list|,
name|Set
argument_list|<
name|?
extends|extends
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
name|this
operator|.
name|userID
operator|=
name|userID
expr_stmt|;
name|this
operator|.
name|attributes
operator|=
operator|(
name|attributes
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
expr|<
name|String
operator|,
name|Object
operator|>
name|emptyMap
argument_list|()
operator|:
name|attributes
expr_stmt|;
name|this
operator|.
name|principals
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|principals
argument_list|)
expr_stmt|;
block|}
comment|//-----------------------------------------------------------< AuthInfo>---
annotation|@
name|Override
specifier|public
name|String
name|getUserID
parameter_list|()
block|{
return|return
name|userID
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getAttributeNames
parameter_list|()
block|{
return|return
name|attributes
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|attributes
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attributeName
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|get
argument_list|(
name|attributeName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Principal
argument_list|>
name|getPrincipals
parameter_list|()
block|{
return|return
name|principals
return|;
block|}
block|}
end_class

end_unit

