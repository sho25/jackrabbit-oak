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
name|multiplex
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|collect
operator|.
name|ImmutableList
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
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|mount
operator|.
name|Mount
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|isAncestor
import|;
end_import

begin_class
specifier|final
class|class
name|MountInfo
implements|implements
name|Mount
block|{
specifier|private
specifier|static
specifier|final
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|SANITIZE_PATH
init|=
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
operator|&&
name|input
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
return|return
name|input
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|input
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
return|return
name|input
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|readOnly
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|defaultMount
decl_stmt|;
specifier|private
specifier|final
name|String
name|pathFragmentName
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|includedPaths
decl_stmt|;
specifier|public
name|MountInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|readOnly
parameter_list|,
name|boolean
name|defaultMount
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|includedPaths
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|,
literal|"Mount name must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|readOnly
operator|=
name|readOnly
expr_stmt|;
name|this
operator|.
name|defaultMount
operator|=
name|defaultMount
expr_stmt|;
name|this
operator|.
name|pathFragmentName
operator|=
literal|"oak:mount-"
operator|+
name|name
expr_stmt|;
name|this
operator|.
name|includedPaths
operator|=
name|cleanCopy
argument_list|(
name|includedPaths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isUnder
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|path
operator|=
name|SANITIZE_PATH
operator|.
name|apply
argument_list|(
name|path
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|includedPath
range|:
name|includedPaths
control|)
block|{
if|if
condition|(
name|isAncestor
argument_list|(
name|path
argument_list|,
name|includedPath
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMounted
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|contains
argument_list|(
name|pathFragmentName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|path
operator|=
name|SANITIZE_PATH
operator|.
name|apply
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|//TODO may be optimized via trie
for|for
control|(
name|String
name|includedPath
range|:
name|includedPaths
control|)
block|{
if|if
condition|(
name|includedPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
name|isAncestor
argument_list|(
name|includedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
name|readOnly
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDefault
parameter_list|()
block|{
return|return
name|defaultMount
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPathFragmentName
parameter_list|()
block|{
return|return
name|pathFragmentName
return|;
block|}
specifier|private
specifier|static
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|cleanCopy
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|includedPaths
parameter_list|)
block|{
comment|// ensure that paths don't have trailing slashes - this triggers an assertion in PahtUtils isAncestor
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|includedPaths
argument_list|,
name|SANITIZE_PATH
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|String
name|readAttr
init|=
name|readOnly
condition|?
literal|"r"
else|:
literal|"rw"
decl_stmt|;
name|String
name|displayName
init|=
name|defaultMount
condition|?
literal|"default"
else|:
name|name
decl_stmt|;
name|pw
operator|.
name|print
argument_list|(
name|displayName
operator|+
literal|"("
operator|+
name|readAttr
operator|+
literal|")"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|includedPaths
control|)
block|{
name|pw
operator|.
name|printf
argument_list|(
literal|"\t%s%n"
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|sw
operator|.
name|toString
argument_list|()
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
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|MountInfo
name|other
init|=
operator|(
name|MountInfo
operator|)
name|obj
decl_stmt|;
return|return
name|name
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

