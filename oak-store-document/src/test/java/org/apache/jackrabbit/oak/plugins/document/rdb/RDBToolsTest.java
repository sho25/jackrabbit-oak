begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|document
operator|.
name|rdb
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|RDBToolsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testAppendInCondition
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|appendInCondition
argument_list|(
literal|"ID"
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ID = ?"
argument_list|,
name|appendInCondition
argument_list|(
literal|"ID"
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ID in (?,?,?)"
argument_list|,
name|appendInCondition
argument_list|(
literal|"ID"
argument_list|,
literal|3
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(ID in (?,?,?) or ID in (?,?,?) or ID in (?,?,?))"
argument_list|,
name|appendInCondition
argument_list|(
literal|"ID"
argument_list|,
literal|9
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(ID in (?,?,?) or ID in (?,?,?) or ID in (?,?,?) or ID in (?,?))"
argument_list|,
name|appendInCondition
argument_list|(
literal|"ID"
argument_list|,
literal|11
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|appendInCondition
parameter_list|(
name|String
name|field
parameter_list|,
name|int
name|placeholdersCount
parameter_list|,
name|int
name|maxListLength
parameter_list|)
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|RDBJDBCTools
operator|.
name|appendInCondition
argument_list|(
name|builder
argument_list|,
name|field
argument_list|,
name|placeholdersCount
argument_list|,
name|maxListLength
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

