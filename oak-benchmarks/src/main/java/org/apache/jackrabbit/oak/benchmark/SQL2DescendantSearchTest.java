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
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
import|;
end_import

begin_comment
comment|/**  * SQL-2 version of the sub-tree performance test.  */
end_comment

begin_class
specifier|public
class|class
name|SQL2DescendantSearchTest
extends|extends
name|DescendantSearchTest
block|{
annotation|@
name|Override
specifier|protected
name|Query
name|createQuery
parameter_list|(
name|QueryManager
name|manager
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|manager
operator|.
name|createQuery
argument_list|(
literal|"SELECT * FROM [nt:base] AS n WHERE ISDESCENDANTNODE(n, '/"
operator|+
name|testNodeName
operator|+
literal|"') AND testcount="
operator|+
name|i
argument_list|,
literal|"JCR-SQL2"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

