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
name|assertArrayEquals
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
name|RDBExportTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testparseDel
parameter_list|()
block|{
comment|// actually, RFC 4180 format
name|String
index|[]
name|empty
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
name|String
index|[]
name|result
decl_stmt|;
name|result
operator|=
name|RDBExport
operator|.
name|parseDel
argument_list|(
literal|"1,2,3"
argument_list|)
operator|.
name|toArray
argument_list|(
name|empty
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|}
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
name|RDBExport
operator|.
name|parseDel
argument_list|(
literal|"\"a\",\"b,c\",\"d\"\"e\""
argument_list|)
operator|.
name|toArray
argument_list|(
name|empty
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b,c"
block|,
literal|"d\"e"
block|}
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

