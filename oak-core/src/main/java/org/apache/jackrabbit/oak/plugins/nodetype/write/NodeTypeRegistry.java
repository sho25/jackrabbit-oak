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
name|nodetype
operator|.
name|write
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|jcr
operator|.
name|NamespaceRegistry
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
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
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
name|commons
operator|.
name|cnd
operator|.
name|CndImporter
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
name|commons
operator|.
name|cnd
operator|.
name|ParseException
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
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|namepath
operator|.
name|impl
operator|.
name|GlobalNameMapper
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
name|namepath
operator|.
name|impl
operator|.
name|NamePathMapperImpl
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
name|name
operator|.
name|ReadWriteNamespaceRegistry
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
name|value
operator|.
name|jcr
operator|.
name|ValueFactoryImpl
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
import|import static
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
import|;
end_import

begin_comment
comment|/**  * {@code BuiltInNodeTypes} is a utility class that registers the built-in  * node types required for a JCR repository running on Oak.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|NodeTypeRegistry
block|{
specifier|private
specifier|final
name|NodeTypeManager
name|ntMgr
decl_stmt|;
specifier|private
specifier|final
name|NamespaceRegistry
name|nsReg
decl_stmt|;
specifier|private
specifier|final
name|ValueFactory
name|vf
decl_stmt|;
specifier|private
name|NodeTypeRegistry
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
name|NotNull
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
name|NotNull
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
name|this
operator|.
name|nsReg
operator|=
operator|new
name|ReadWriteNamespaceRegistry
argument_list|(
name|root
argument_list|)
block|{
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
name|this
operator|.
name|vf
operator|=
operator|new
name|ValueFactoryImpl
argument_list|(
name|root
argument_list|,
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|GlobalNameMapper
argument_list|(
name|root
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Register the node type definitions contained in the specified {@code input}      * using the given {@link Root}.      *      * @param root The {@code Root} to register the node types.      * @param input The input stream containing the node type defintions to be registered.      * @param systemId An informative id of the given input.      */
specifier|public
specifier|static
name|void
name|register
parameter_list|(
name|Root
name|root
parameter_list|,
name|InputStream
name|input
parameter_list|,
name|String
name|systemId
parameter_list|)
block|{
operator|new
name|NodeTypeRegistry
argument_list|(
name|root
argument_list|)
operator|.
name|registerNodeTypes
argument_list|(
name|input
argument_list|,
name|systemId
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|registerNodeTypes
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|String
name|systemId
parameter_list|)
block|{
try|try
block|{
name|CndImporter
operator|.
name|registerNodeTypes
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|,
name|systemId
argument_list|,
name|ntMgr
argument_list|,
name|nsReg
argument_list|,
name|vf
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to read "
operator|+
name|systemId
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to parse "
operator|+
name|systemId
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to register "
operator|+
name|systemId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

