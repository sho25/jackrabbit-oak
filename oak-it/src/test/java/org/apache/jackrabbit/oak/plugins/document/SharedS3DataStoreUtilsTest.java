begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
import|import
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
name|s3
operator|.
name|S3Constants
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
name|blob
operator|.
name|cloud
operator|.
name|s3
operator|.
name|S3DataStoreUtils
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
name|blob
operator|.
name|SharedDataStoreUtilsTest
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|blob
operator|.
name|cloud
operator|.
name|s3
operator|.
name|S3DataStoreUtils
operator|.
name|deleteBucket
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * SharedDataStoreUtilsTest for S3.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|SharedS3DataStoreUtilsTest
extends|extends
name|SharedDataStoreUtilsTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SharedS3DataStoreUtilsTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
specifier|public
name|String
name|s3Class
decl_stmt|;
specifier|protected
name|String
name|bucket
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{index}: ({0})"
argument_list|)
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|fixtures
parameter_list|()
block|{
return|return
name|S3DataStoreUtils
operator|.
name|getFixtures
argument_list|()
return|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|assumptions
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|S3DataStoreUtils
operator|.
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|DataStoreBlobStore
name|getBlobStore
parameter_list|(
name|File
name|rootFolder
parameter_list|)
throws|throws
name|Exception
block|{
name|Properties
name|props
init|=
name|S3DataStoreUtils
operator|.
name|getS3Config
argument_list|()
decl_stmt|;
name|bucket
operator|=
name|rootFolder
operator|.
name|getName
argument_list|()
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|S3Constants
operator|.
name|S3_BUCKET
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"cacheSize"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|S3Constants
operator|.
name|MAX_KEYS
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
return|return
operator|new
name|DataStoreBlobStore
argument_list|(
name|S3DataStoreUtils
operator|.
name|getS3DataStore
argument_list|(
name|s3Class
argument_list|,
name|props
argument_list|,
name|rootFolder
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|deleteBucket
argument_list|(
name|bucket
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error closing data store"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

