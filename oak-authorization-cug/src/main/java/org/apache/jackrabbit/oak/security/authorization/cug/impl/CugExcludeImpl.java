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
name|authorization
operator|.
name|cug
operator|.
name|impl
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|principal
operator|.
name|ItemBasedPrincipal
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
name|commons
operator|.
name|PropertiesUtil
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
name|security
operator|.
name|authorization
operator|.
name|cug
operator|.
name|CugExclude
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
name|principal
operator|.
name|PrincipalImpl
import|;
end_import

begin_comment
comment|/**  * CugExcludeImpl... TODO  */
end_comment

begin_class
annotation|@
name|Component
argument_list|()
annotation|@
name|Service
argument_list|(
block|{
name|CugExclude
operator|.
name|class
block|}
argument_list|)
annotation|@
name|Properties
argument_list|(
block|{
annotation|@
name|Property
argument_list|(
name|name
operator|=
literal|"principalNames"
argument_list|,
name|label
operator|=
literal|"Principal Names"
argument_list|,
name|description
operator|=
literal|"Name of principals that are always excluded from CUG evaluation."
argument_list|,
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
block|,
annotation|@
name|Property
argument_list|(
name|name
operator|=
literal|"principalPaths"
argument_list|,
name|label
operator|=
literal|"Principal Paths"
argument_list|,
name|description
operator|=
literal|"Path pattern for principals that are always excluded from CUG evaluation"
argument_list|,
name|cardinality
operator|=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|CugExcludeImpl
extends|extends
name|CugExclude
operator|.
name|Default
block|{
specifier|private
name|String
index|[]
name|principalNames
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|String
index|[]
name|principalPaths
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|isExcluded
parameter_list|(
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|isExcluded
argument_list|(
name|principals
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|String
name|principalName
range|:
name|principalNames
control|)
block|{
if|if
condition|(
name|principals
operator|.
name|contains
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
name|principalName
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
name|principalPaths
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|principalPath
range|:
name|getPrincipalPaths
argument_list|(
name|principals
argument_list|)
control|)
block|{
for|for
control|(
name|String
name|path
range|:
name|principalPaths
control|)
block|{
if|if
condition|(
name|principalPath
operator|.
name|startsWith
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Activate
specifier|protected
name|void
name|activate
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
name|principalNames
operator|=
name|PropertiesUtil
operator|.
name|toStringArray
argument_list|(
name|properties
operator|.
name|get
argument_list|(
literal|"principalNames"
argument_list|)
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|principalPaths
operator|=
name|PropertiesUtil
operator|.
name|toStringArray
argument_list|(
name|properties
operator|.
name|get
argument_list|(
literal|"principalPaths"
argument_list|)
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Iterable
argument_list|<
name|String
argument_list|>
name|getPrincipalPaths
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Iterable
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|principals
argument_list|,
operator|new
name|Function
argument_list|<
name|Principal
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Principal
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|instanceof
name|ItemBasedPrincipal
condition|)
block|{
try|try
block|{
return|return
operator|(
operator|(
name|ItemBasedPrincipal
operator|)
name|input
operator|)
operator|.
name|getPath
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
argument_list|)
argument_list|,
name|Predicates
operator|.
name|notNull
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

