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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|Modified
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
name|spi
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * Extension of the default {@link org.apache.jackrabbit.oak.spi.security.authorization.cug.CugExclude}  * implementation that allow to specify additional principal names to be excluded  * from CUG evaluation.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|metatype
operator|=
literal|true
argument_list|,
name|immediate
operator|=
literal|true
argument_list|,
name|label
operator|=
literal|"Apache Jackrabbit Oak CUG Exclude List"
argument_list|,
name|description
operator|=
literal|"Exclude principal(s) from CUG evaluation. In addition to the "
operator|+
literal|"principals defined by the default CugExclude ('AdminPrincipal', 'SystemPrincipal', 'SystemUserPrincipal' classes), "
operator|+
literal|"this component allows to optionally configure additional principals by name."
argument_list|)
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
literal|"Name(s) of additional principal(s) that are excluded from CUG evaluation."
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
name|Set
argument_list|<
name|String
argument_list|>
name|principalNames
init|=
name|Collections
operator|.
name|emptySet
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|isExcluded
parameter_list|(
annotation|@
name|NotNull
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
if|if
condition|(
operator|!
name|principalNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Principal
name|p
range|:
name|principals
control|)
block|{
if|if
condition|(
name|principalNames
operator|.
name|contains
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
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
name|setPrincipalNames
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Modified
specifier|protected
name|void
name|modified
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
name|setPrincipalNames
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setPrincipalNames
parameter_list|(
annotation|@
name|NotNull
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
block|{
name|this
operator|.
name|principalNames
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
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
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

