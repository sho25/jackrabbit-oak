begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|plugins
operator|.
name|index
operator|.
name|lucene
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
name|PropertyState
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
name|PathUtils
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
name|state
operator|.
name|NodeState
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
operator|.
name|copyOf
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|toArray
import|;
end_import

begin_import
import|import static
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
name|PathUtils
operator|.
name|elements
import|;
end_import

begin_import
import|import static
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
name|PathUtils
operator|.
name|isAbsolute
import|;
end_import

begin_class
class|class
name|RelativeProperty
block|{
specifier|final
name|String
name|propertyPath
decl_stmt|;
specifier|final
name|String
name|parentPath
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
comment|/**      * Stores the parent path element in reverse order      * parentPath -> foo/bar/baz -> [baz, bar, foo]      */
specifier|final
name|String
index|[]
name|ancestors
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|isRelativeProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
return|return
operator|!
name|isAbsolute
argument_list|(
name|propertyName
argument_list|)
operator|&&
name|PathUtils
operator|.
name|getNextSlash
argument_list|(
name|propertyName
argument_list|,
literal|0
argument_list|)
operator|>
literal|0
return|;
block|}
specifier|public
name|RelativeProperty
parameter_list|(
name|String
name|propertyPath
parameter_list|)
block|{
name|this
operator|.
name|propertyPath
operator|=
name|propertyPath
expr_stmt|;
name|name
operator|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|propertyPath
argument_list|)
expr_stmt|;
name|parentPath
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|propertyPath
argument_list|)
expr_stmt|;
name|ancestors
operator|=
name|computeAncestors
argument_list|(
name|parentPath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
name|NodeState
name|getPropDefnNode
parameter_list|(
name|NodeState
name|propNode
parameter_list|)
block|{
name|NodeState
name|result
init|=
name|propNode
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|propertyPath
argument_list|)
control|)
block|{
name|result
operator|=
name|result
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|NodeState
name|node
init|=
name|state
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|parentPath
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|node
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|node
operator|.
name|exists
argument_list|()
condition|?
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|RelativeProperty
name|that
init|=
operator|(
name|RelativeProperty
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|propertyPath
operator|.
name|equals
argument_list|(
name|that
operator|.
name|propertyPath
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|propertyPath
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|propertyPath
return|;
block|}
specifier|private
name|String
index|[]
name|computeAncestors
parameter_list|(
name|String
name|parentPath
parameter_list|)
block|{
return|return
name|toArray
argument_list|(
name|copyOf
argument_list|(
name|elements
argument_list|(
name|parentPath
argument_list|)
argument_list|)
operator|.
name|reverse
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

