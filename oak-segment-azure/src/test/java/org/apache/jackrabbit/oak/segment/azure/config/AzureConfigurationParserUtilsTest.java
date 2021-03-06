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
name|segment
operator|.
name|azure
operator|.
name|config
package|;
end_package

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
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_ACCOUNT_NAME
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
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_CONNECTION_STRING
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
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_CONTAINER_NAME
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
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_DIR
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
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
operator|.
name|KEY_STORAGE_URI
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
name|assertEquals
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
name|assertFalse
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|segment
operator|.
name|azure
operator|.
name|util
operator|.
name|AzureConfigurationParserUtils
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
name|AzureConfigurationParserUtilsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testParseConnectionDetailsFromCustomConnection
parameter_list|()
block|{
name|StringBuilder
name|conn
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|StringBuilder
name|connStr
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|connStr
operator|.
name|append
argument_list|(
literal|"DefaultEndpointsProtocol=https;"
argument_list|)
expr_stmt|;
name|connStr
operator|.
name|append
argument_list|(
literal|"AccountName=myaccount;"
argument_list|)
expr_stmt|;
name|connStr
operator|.
name|append
argument_list|(
literal|"AccountKey=mykey==;"
argument_list|)
expr_stmt|;
name|connStr
operator|.
name|append
argument_list|(
literal|"BlobEndpoint=http://127.0.0.1:32806/myaccount;"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|append
argument_list|(
name|connStr
argument_list|)
expr_stmt|;
name|conn
operator|.
name|append
argument_list|(
literal|"ContainerName=oak-test;"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|append
argument_list|(
literal|"Directory=repository"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|AzureConfigurationParserUtils
operator|.
name|isCustomAzureConnectionString
argument_list|(
name|conn
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|config
init|=
name|AzureConfigurationParserUtils
operator|.
name|parseAzureConfigurationFromCustomConnection
argument_list|(
name|conn
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|connStr
operator|.
name|toString
argument_list|()
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|KEY_CONNECTION_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak-test"
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|KEY_CONTAINER_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"repository"
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|KEY_DIR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParseConnectionDetailsFromCustomConnectionShuffledKeys
parameter_list|()
block|{
name|StringBuilder
name|conn
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|conn
operator|.
name|append
argument_list|(
literal|"Directory=repository;"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|append
argument_list|(
literal|"DefaultEndpointsProtocol=https;"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|append
argument_list|(
literal|"ContainerName=oak-test;"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|append
argument_list|(
literal|"AccountName=myaccount;"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|append
argument_list|(
literal|"BlobEndpoint=http://127.0.0.1:32806/myaccount;"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|append
argument_list|(
literal|"AccountKey=mykey=="
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|AzureConfigurationParserUtils
operator|.
name|isCustomAzureConnectionString
argument_list|(
name|conn
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|azureConn
init|=
literal|"DefaultEndpointsProtocol=https;AccountName=myaccount;AccountKey=mykey==;BlobEndpoint=http://127.0.0.1:32806/myaccount;"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|config
init|=
name|AzureConfigurationParserUtils
operator|.
name|parseAzureConfigurationFromCustomConnection
argument_list|(
name|conn
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|azureConn
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|KEY_CONNECTION_STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak-test"
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|KEY_CONTAINER_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"repository"
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|KEY_DIR
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParseConnectionDetailsFromUri
parameter_list|()
block|{
name|String
name|uri
init|=
literal|"https://myaccount.blob.core.windows.net/oak-test/repository"
decl_stmt|;
name|assertFalse
argument_list|(
name|AzureConfigurationParserUtils
operator|.
name|isCustomAzureConnectionString
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|config
init|=
name|AzureConfigurationParserUtils
operator|.
name|parseAzureConfigurationFromUri
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"myaccount"
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|KEY_ACCOUNT_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"https://myaccount.blob.core.windows.net/oak-test"
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|KEY_STORAGE_URI
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"repository"
argument_list|,
name|config
operator|.
name|get
argument_list|(
name|KEY_DIR
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

