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

begin_comment
comment|/**  * Convenience class that dumps the table creation statements for various  * database types.  */
end_comment

begin_class
specifier|public
class|class
name|RDBHelper
block|{
specifier|private
specifier|static
name|String
index|[]
name|databases
init|=
block|{
literal|"Apache Derby"
block|,
literal|"DB2"
block|,
literal|"H2"
block|,
literal|"Microsoft SQL Server"
block|,
literal|"MySQL"
block|,
literal|"Oracle"
block|,
literal|"PostgreSQL"
block|,
literal|"default"
block|}
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
for|for
control|(
name|String
name|database
range|:
name|databases
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|RDBDocumentStore
operator|.
name|DB
name|ddb
init|=
name|RDBDocumentStore
operator|.
name|DB
operator|.
name|getValue
argument_list|(
name|database
argument_list|)
decl_stmt|;
name|RDBBlobStore
operator|.
name|DB
name|bdb
init|=
name|RDBBlobStore
operator|.
name|DB
operator|.
name|getValue
argument_list|(
name|database
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|table
range|:
name|RDBDocumentStore
operator|.
name|getTableNames
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|ddb
operator|.
name|getTableCreationStatement
argument_list|(
name|table
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|ddb
operator|.
name|getIndexCreationStatements
argument_list|(
name|table
argument_list|)
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|bdb
operator|.
name|getMetaTableCreationStatement
argument_list|(
literal|"DATASTORE_META"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|bdb
operator|.
name|getDataTableCreationStatement
argument_list|(
literal|"DATASTORE_DATA"
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

