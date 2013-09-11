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
name|benchmark
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
name|javax
operator|.
name|jcr
operator|.
name|ImportUUIDBehavior
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_class
specifier|public
class|class
name|XmlImportTest
extends|extends
name|AbstractTest
block|{
specifier|private
name|Session
name|adminSession
decl_stmt|;
specifier|private
name|Node
name|testRoot
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|in
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"deepTree.xml"
argument_list|)
decl_stmt|;
name|adminSession
operator|.
name|importXML
argument_list|(
name|testRoot
operator|.
name|getPath
argument_list|()
argument_list|,
name|in
argument_list|,
name|ImportUUIDBehavior
operator|.
name|IMPORT_UUID_CREATE_NEW
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beforeTest
parameter_list|()
throws|throws
name|Exception
block|{
name|adminSession
operator|=
name|loginWriter
argument_list|()
expr_stmt|;
name|String
name|name
init|=
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|testRoot
operator|=
name|adminSession
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|name
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterTest
parameter_list|()
throws|throws
name|Exception
block|{
name|testRoot
operator|.
name|remove
argument_list|()
expr_stmt|;
name|adminSession
operator|.
name|save
argument_list|()
expr_stmt|;
name|adminSession
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

