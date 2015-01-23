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
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_class
specifier|public
class|class
name|RDBCreator
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|ClassNotFoundException
throws|,
name|SQLException
block|{
name|String
name|url
init|=
literal|null
decl_stmt|,
name|user
init|=
literal|null
decl_stmt|,
name|pw
init|=
literal|null
decl_stmt|,
name|db
init|=
literal|null
decl_stmt|;
try|try
block|{
name|url
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
name|user
operator|=
name|args
index|[
literal|1
index|]
expr_stmt|;
name|pw
operator|=
name|args
index|[
literal|2
index|]
expr_stmt|;
name|db
operator|=
name|args
index|[
literal|3
index|]
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: ... "
operator|+
name|RDBCreator
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" JDBC-URL username password databasename"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|String
name|driver
init|=
name|RDBJDBCTools
operator|.
name|driverForDBType
argument_list|(
name|url
argument_list|)
decl_stmt|;
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|ex
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Attempt to load class "
operator|+
name|driver
operator|+
literal|" failed."
argument_list|)
expr_stmt|;
block|}
name|Connection
name|c
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|,
name|user
argument_list|,
name|pw
argument_list|)
decl_stmt|;
name|Statement
name|stmt
init|=
name|c
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|stmt
operator|.
name|execute
argument_list|(
literal|"create database "
operator|+
name|db
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Database "
operator|+
name|db
operator|+
literal|" created @ "
operator|+
name|url
operator|+
literal|" using "
operator|+
name|driver
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

