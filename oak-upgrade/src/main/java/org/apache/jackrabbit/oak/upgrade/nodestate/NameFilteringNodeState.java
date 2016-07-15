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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
specifier|static
specifier|final
name|int
name|NODE_NAME_LIMIT
init|=
literal|150
decl_stmt|;
comment|/**      * Max character size in bytes in UTF8 = 4. Therefore if the number of characters is smaller      * than NODE_NAME_LIMIT / 4 we don't need to count bytes.      */
specifier|private
specifier|static
specifier|final
name|int
name|SAFE_NODE_NAME_LENGTH
init|=
name|NODE_NAME_LIMIT
operator|/
literal|4
decl_stmt|;
specifier|public
specifier|static
name|NodeState
name|wrap
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
argument_list|)
return|;
block|}
specifier|private
name|NameFilteringNodeState
parameter_list|(
specifier|final
name|NodeState
name|delegate
parameter_list|)
block|{
name|super
argument_list|(
name|delegate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|hideChild
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
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
name|Nonnull
specifier|protected
name|NodeState
name|decorateChild
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|NodeState
name|delegateChild
parameter_list|)
block|{
return|return
name|wrap
argument_list|(
name|delegateChild
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
name|Nonnull
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
comment|/**      * This method checks whether the name is no longer than the maximum node      * name length supported by the DocumentNodeStore.      *      * @param name      *            to check      * @return true if the name is longer than {@link Utils#NODE_NAME_LIMIT}      */
specifier|public
specifier|static
name|boolean
name|isNameTooLong
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
comment|// OAK-1589: maximum supported length of name for DocumentNodeStore
comment|// is 150 bytes. Skip the sub tree if the the name is too long
return|return
name|name
operator|.
name|length
argument_list|()
operator|>
name|SAFE_NODE_NAME_LENGTH
operator|&&
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
operator|>
name|NODE_NAME_LIMIT
return|;
block|}
block|}
end_class

end_unit

