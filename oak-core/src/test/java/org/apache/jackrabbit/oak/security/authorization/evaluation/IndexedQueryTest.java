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
name|evaluation
package|;
end_package

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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|index
operator|.
name|IndexUtils
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|IndexedQueryTest
extends|extends
name|AbstractQueryTest
block|{
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"title"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|subnode
operator|.
name|setProperty
argument_list|(
literal|"title"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|grantPropertyReadAccess
argument_list|(
literal|"title"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|createIndexDefinition
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Tree
name|oakIndex
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
operator|+
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|oakIndex
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|oakIndex
argument_list|,
literal|"test-index"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"title"
block|}
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
block|}
name|String
name|getStatement
parameter_list|()
block|{
return|return
literal|"SELECT * FROM [nt:unstructured] WHERE [title] is not null"
return|;
block|}
block|}
end_class

end_unit

