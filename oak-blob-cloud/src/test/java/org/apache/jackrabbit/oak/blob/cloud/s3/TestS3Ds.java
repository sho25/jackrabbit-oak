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
name|s3
package|;
end_package

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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
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
name|core
operator|.
name|data
operator|.
name|DataStore
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
name|aws
operator|.
name|s3
operator|.
name|S3DataStore
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
name|AbstractDataStoreTest
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
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|mockito
operator|.
name|internal
operator|.
name|matchers
operator|.
name|Equals
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
name|getFixtures
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
name|getS3Config
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
name|getS3DataStore
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
name|isS3Configured
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
comment|/**  * Test {@link S3DataStore} with S3Backend and local cache on.  * It requires to pass aws config file via system property or system properties by prefixing with 'ds.'.  * See details @ {@link S3DataStoreUtils}.  * For e.g. -Dconfig=/opt/cq/aws.properties. Sample aws properties located at  * src/test/resources/aws.properties  */
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
name|TestS3Ds
extends|extends
name|AbstractDataStoreTest
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestS3Ds
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Date
name|startTime
init|=
literal|null
decl_stmt|;
specifier|protected
name|Properties
name|props
decl_stmt|;
specifier|protected
name|String
name|bucket
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
specifier|public
name|String
name|s3Class
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
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|props
operator|=
name|getS3Config
argument_list|()
expr_stmt|;
name|startTime
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|bucket
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|randomGen
operator|.
name|nextInt
argument_list|(
literal|9999
argument_list|)
argument_list|)
operator|+
literal|"-"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|randomGen
operator|.
name|nextInt
argument_list|(
literal|9999
argument_list|)
argument_list|)
operator|+
literal|"-test"
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
literal|"secret"
argument_list|,
literal|"123456"
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|S3DataStoreUtils
operator|.
name|deleteBucket
argument_list|(
name|bucket
argument_list|,
name|startTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{          }
block|}
specifier|protected
name|DataStore
name|createDataStore
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|DataStore
name|s3ds
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s3ds
operator|=
name|getS3DataStore
argument_list|(
name|s3Class
argument_list|,
name|props
argument_list|,
name|dataStoreDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
return|return
name|s3ds
return|;
block|}
comment|/**----------Only run with org.apache.jackrabbit.oak.blob.cloud.aws.s3.S3DataStore-----------**/
annotation|@
name|Override
specifier|public
name|void
name|testUpdateLastModifiedOnAccess
parameter_list|()
block|{
name|Assume
operator|.
name|assumeThat
argument_list|(
name|s3Class
argument_list|,
operator|new
name|Equals
argument_list|(
name|fixtures
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|testUpdateLastModifiedOnAccess
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|testDeleteAllOlderThan
parameter_list|()
block|{
name|Assume
operator|.
name|assumeThat
argument_list|(
name|s3Class
argument_list|,
operator|new
name|Equals
argument_list|(
name|fixtures
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|testDeleteAllOlderThan
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

