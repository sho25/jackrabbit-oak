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
name|blob
operator|.
name|cloud
operator|.
name|azure
operator|.
name|blobstorage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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

begin_class
specifier|public
class|class
name|UtilsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testConnectionStringIsBasedOnSAS
parameter_list|()
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|AzureConstants
operator|.
name|AZURE_SAS
argument_list|,
literal|"sas"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|AzureConstants
operator|.
name|AZURE_BLOB_ENDPOINT
argument_list|,
literal|"endpoint"
argument_list|)
expr_stmt|;
name|String
name|connectionString
init|=
name|Utils
operator|.
name|getConnectionStringFromProperties
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|connectionString
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"BlobEndpoint=%s;SharedAccessSignature=%s"
argument_list|,
literal|"endpoint"
argument_list|,
literal|"sas"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConnectionStringIsBasedOnAccessKeyIfSASMissing
parameter_list|()
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|AzureConstants
operator|.
name|AZURE_STORAGE_ACCOUNT_NAME
argument_list|,
literal|"accessKey"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|AzureConstants
operator|.
name|AZURE_STORAGE_ACCOUNT_KEY
argument_list|,
literal|"secretKey"
argument_list|)
expr_stmt|;
name|String
name|connectionString
init|=
name|Utils
operator|.
name|getConnectionStringFromProperties
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|connectionString
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s"
argument_list|,
literal|"accessKey"
argument_list|,
literal|"secretKey"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConnectionStringSASIsPriority
parameter_list|()
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|AzureConstants
operator|.
name|AZURE_SAS
argument_list|,
literal|"sas"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|AzureConstants
operator|.
name|AZURE_BLOB_ENDPOINT
argument_list|,
literal|"endpoint"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|AzureConstants
operator|.
name|AZURE_STORAGE_ACCOUNT_NAME
argument_list|,
literal|"accessKey"
argument_list|)
expr_stmt|;
name|properties
operator|.
name|put
argument_list|(
name|AzureConstants
operator|.
name|AZURE_STORAGE_ACCOUNT_KEY
argument_list|,
literal|"secretKey"
argument_list|)
expr_stmt|;
name|String
name|connectionString
init|=
name|Utils
operator|.
name|getConnectionStringFromProperties
argument_list|(
name|properties
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|connectionString
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"BlobEndpoint=%s;SharedAccessSignature=%s"
argument_list|,
literal|"endpoint"
argument_list|,
literal|"sas"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

