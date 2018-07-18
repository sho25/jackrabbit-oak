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
name|upgrade
operator|.
name|nodestate
package|;
end_package

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
name|Charsets
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
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|Utils
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
name|plugins
operator|.
name|migration
operator|.
name|AbstractDecoratedNodeState
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_class
specifier|public
class|class
name|NameFilteringNodeState
extends|extends
name|AbstractDecoratedNodeState
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NameFilteringNodeState
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NameFilteringNodeState
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
specifier|static
name|NodeState
name|wrapRoot
parameter_list|(
specifier|final
name|NodeState
name|delegate
parameter_list|)
block|{
return|return
operator|new
name|NameFilteringNodeState
argument_list|(
name|delegate
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
name|NameFilteringNodeState
parameter_list|(
specifier|final
name|NodeState
name|delegate
parameter_list|,
name|NameFilteringNodeState
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|hideChild
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|NodeState
name|delegateChild
parameter_list|)
block|{
if|if
condition|(
name|isNameTooLong
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Node name '{}' too long. Skipping child of {}"
argument_list|,
name|name
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
name|super
operator|.
name|hideChild
argument_list|(
name|name
argument_list|,
name|delegateChild
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|protected
name|NodeState
name|decorateChild
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|NodeState
name|delegateChild
parameter_list|)
block|{
return|return
operator|new
name|NameFilteringNodeState
argument_list|(
name|delegateChild
argument_list|,
name|this
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|PropertyState
name|decorateProperty
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|PropertyState
name|delegatePropertyState
parameter_list|)
block|{
return|return
name|fixChildOrderPropertyState
argument_list|(
name|this
argument_list|,
name|delegatePropertyState
argument_list|)
return|;
block|}
comment|/**      * This method checks whether the name is no longer than the maximum node      * name length supported by the DocumentNodeStore.      *      * @param name      *            to check      * @return true if the name is longer than {@link org.apache.jackrabbit.oak.plugins.document.util.Utils#NODE_NAME_LIMIT}      */
specifier|private
name|boolean
name|isNameTooLong
parameter_list|(
annotation|@
name|NotNull
name|String
name|name
parameter_list|)
block|{
comment|// OAK-1589: maximum supported length of name for DocumentNodeStore
comment|// is 150 bytes. Skip the sub tree if the the name is too long
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|<=
name|Utils
operator|.
name|NODE_NAME_LIMIT
operator|/
literal|3
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|name
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|length
operator|<=
name|Utils
operator|.
name|NODE_NAME_LIMIT
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|path
init|=
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|<=
name|Utils
operator|.
name|PATH_SHORT
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|path
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|length
operator|<
name|Utils
operator|.
name|PATH_LONG
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|String
name|getPath
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|NameFilteringNodeState
name|ns
init|=
name|this
decl_stmt|;
while|while
condition|(
name|ns
operator|.
name|parent
operator|!=
literal|null
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|ns
operator|.
name|name
argument_list|)
expr_stmt|;
name|ns
operator|=
name|ns
operator|.
name|parent
expr_stmt|;
block|}
name|String
index|[]
name|reversed
init|=
operator|new
name|String
index|[
name|names
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
name|reversed
operator|.
name|length
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|reversed
index|[
name|i
operator|--
index|]
operator|=
name|name
expr_stmt|;
block|}
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|reversed
argument_list|)
return|;
block|}
block|}
end_class

end_unit

