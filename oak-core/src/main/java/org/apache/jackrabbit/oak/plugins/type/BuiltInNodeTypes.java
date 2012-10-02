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
name|plugins
operator|.
name|type
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|Root
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
name|Tree
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
name|plugins
operator|.
name|type
operator|.
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
import|;
end_import

begin_comment
comment|/**  *<code>BuiltInNodeTypes</code> is a utility class that registers the built-in  * node types required for a JCR repository running on Oak.  */
end_comment

begin_class
specifier|public
class|class
name|BuiltInNodeTypes
block|{
specifier|private
specifier|final
name|ReadWriteNodeTypeManager
name|ntMgr
decl_stmt|;
specifier|private
name|BuiltInNodeTypes
parameter_list|(
specifier|final
name|Root
name|root
parameter_list|)
block|{
name|this
operator|.
name|ntMgr
operator|=
operator|new
name|ReadWriteNodeTypeManager
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Tree
name|getTypes
parameter_list|()
block|{
return|return
name|root
operator|.
name|getTree
argument_list|(
name|NODE_TYPES_PATH
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|Root
name|getWriteRoot
parameter_list|()
block|{
return|return
name|root
return|;
block|}
block|}
expr_stmt|;
block|}
comment|/**      * Registers built in node types using the given {@link Root}.      *      * @param root the {@link Root} instance.      */
specifier|public
specifier|static
name|void
name|register
parameter_list|(
specifier|final
name|Root
name|root
parameter_list|)
block|{
operator|new
name|BuiltInNodeTypes
argument_list|(
name|root
argument_list|)
operator|.
name|registerBuiltinNodeTypes
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|registerBuiltinNodeTypes
parameter_list|()
block|{
comment|// FIXME: migrate custom node types as well.
if|if
condition|(
operator|!
name|nodeTypesInContent
argument_list|()
condition|)
block|{
try|try
block|{
name|InputStream
name|stream
init|=
name|BuiltInNodeTypes
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"builtin_nodetypes.cnd"
argument_list|)
decl_stmt|;
try|try
block|{
name|ntMgr
operator|.
name|registerNodeTypes
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to load built-in node types"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|boolean
name|nodeTypesInContent
parameter_list|()
block|{
name|Tree
name|types
init|=
name|ntMgr
operator|.
name|getTypes
argument_list|()
decl_stmt|;
return|return
name|types
operator|!=
literal|null
operator|&&
name|types
operator|.
name|getChildrenCount
argument_list|()
operator|>
literal|0
return|;
block|}
block|}
end_class

end_unit

