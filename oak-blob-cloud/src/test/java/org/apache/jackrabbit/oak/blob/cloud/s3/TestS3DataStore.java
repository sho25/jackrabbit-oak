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
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Hex
operator|.
name|encodeHexString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
operator|.
name|copyInputStreamToFile
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
name|DataStoreUtils
operator|.
name|randomStream
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
name|assertNotNull
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|Mac
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|crypto
operator|.
name|spec
operator|.
name|SecretKeySpec
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|time
operator|.
name|DateUtils
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
name|DataRecord
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
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

begin_comment
comment|/**  * Simple tests for S3DataStore.  */
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
name|TestS3DataStore
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
name|TestS3DataStore
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|ExpectedException
name|expectedEx
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|Properties
name|props
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
specifier|public
name|String
name|s3Class
decl_stmt|;
specifier|private
name|File
name|dataStoreDir
decl_stmt|;
specifier|private
name|DataStore
name|ds
decl_stmt|;
specifier|private
name|Date
name|startTime
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
specifier|private
name|String
name|bucket
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Backdate by 1 minute, to allow for time drift when deleting
comment|// resources created by the test.
name|startTime
operator|=
name|DateUtils
operator|.
name|addMinutes
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|dataStoreDir
operator|=
name|folder
operator|.
name|newFolder
argument_list|()
expr_stmt|;
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAccessParamLeakOnError
parameter_list|()
throws|throws
name|Exception
block|{
name|expectedEx
operator|.
name|expect
argument_list|(
name|RepositoryException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedEx
operator|.
name|expectMessage
argument_list|(
literal|"Could not initialize S3 from {s3Region=us-standard, intValueKey=25}"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|S3Constants
operator|.
name|ACCESS_KEY
argument_list|,
literal|"abcd"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|S3Constants
operator|.
name|SECRET_KEY
argument_list|,
literal|"123456"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|S3Constants
operator|.
name|S3_REGION
argument_list|,
literal|"us-standard"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"intValueKey"
argument_list|,
literal|25
argument_list|)
expr_stmt|;
name|ds
operator|=
name|getS3DataStore
argument_list|(
name|s3Class
argument_list|,
name|props
argument_list|,
name|dataStoreDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSecret
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|Random
name|randomGen
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|props
operator|=
name|S3DataStoreUtils
operator|.
name|getS3Config
argument_list|()
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"cacheSize"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|ds
operator|=
name|getS3DataStore
argument_list|(
name|s3Class
argument_list|,
name|props
argument_list|,
name|dataStoreDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|bucket
operator|=
name|props
operator|.
name|getProperty
argument_list|(
name|S3Constants
operator|.
name|S3_BUCKET
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|randomGen
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|DataRecord
name|rec
init|=
name|ds
operator|.
name|addRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|data
operator|.
name|length
argument_list|,
name|rec
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|ref
init|=
name|rec
operator|.
name|getReference
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|String
name|id
init|=
name|rec
operator|.
name|getIdentifier
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|S3DataStore
name|s3
init|=
operator|(
operator|(
name|S3DataStore
operator|)
name|ds
operator|)
decl_stmt|;
name|byte
index|[]
name|refKey
init|=
operator|(
operator|(
name|S3Backend
operator|)
name|s3
operator|.
name|getBackend
argument_list|()
operator|)
operator|.
name|getOrCreateReferenceKey
argument_list|()
decl_stmt|;
name|Mac
name|mac
init|=
name|Mac
operator|.
name|getInstance
argument_list|(
literal|"HmacSHA1"
argument_list|)
decl_stmt|;
name|mac
operator|.
name|init
argument_list|(
operator|new
name|SecretKeySpec
argument_list|(
name|refKey
argument_list|,
literal|"HmacSHA1"
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|hash
init|=
name|mac
operator|.
name|doFinal
argument_list|(
name|id
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|calcRef
init|=
name|id
operator|+
literal|':'
operator|+
name|encodeHexString
argument_list|(
name|hash
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"getReference() not equal"
argument_list|,
name|calcRef
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|DataRecord
name|refRec
init|=
name|s3
operator|.
name|getMetadataRecord
argument_list|(
literal|"reference.key"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Reference data record null"
argument_list|,
name|refRec
argument_list|)
expr_stmt|;
name|byte
index|[]
name|refDirectFromBackend
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|refRec
operator|.
name|getStream
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ref direct from backend {}"
argument_list|,
name|refDirectFromBackend
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"refKey in memory not equal to the metadata record"
argument_list|,
name|Arrays
operator|.
name|equals
argument_list|(
name|refKey
argument_list|,
name|refDirectFromBackend
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAlternateBucketProp
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|Random
name|randomGen
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|props
operator|=
name|S3DataStoreUtils
operator|.
name|getS3Config
argument_list|()
expr_stmt|;
comment|//Replace bucket in props with container
name|bucket
operator|=
name|props
operator|.
name|getProperty
argument_list|(
name|S3Constants
operator|.
name|S3_BUCKET
argument_list|)
expr_stmt|;
name|props
operator|.
name|remove
argument_list|(
name|S3Constants
operator|.
name|S3_BUCKET
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|S3Constants
operator|.
name|S3_CONTAINER
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"cacheSize"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|ds
operator|=
name|getS3DataStore
argument_list|(
name|s3Class
argument_list|,
name|props
argument_list|,
name|dataStoreDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|randomGen
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|DataRecord
name|rec
init|=
name|ds
operator|.
name|addRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|data
operator|.
name|length
argument_list|,
name|rec
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// AddMetadataRecord (Backend)
annotation|@
name|Test
specifier|public
name|void
name|testBackendAddMetadataRecordsFromInputStream
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
for|for
control|(
name|boolean
name|fromInputStream
range|:
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
control|)
block|{
name|String
name|prefix
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%s.META."
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|count
range|:
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|records
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|recordName
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%sname.%d"
argument_list|,
name|prefix
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|String
name|data
init|=
name|String
operator|.
name|format
argument_list|(
literal|"testData%d"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|records
operator|.
name|put
argument_list|(
name|recordName
argument_list|,
name|data
argument_list|)
expr_stmt|;
if|if
condition|(
name|fromInputStream
condition|)
block|{
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|recordName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|File
name|testFile
init|=
name|folder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|copyInputStreamToFile
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|testFile
argument_list|,
name|recordName
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|count
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
name|prefix
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|records
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DataRecord
name|record
init|=
name|s3ds
operator|.
name|getMetadataRecord
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|record
operator|.
name|getStream
argument_list|()
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|deleteMetadataRecord
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
name|prefix
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBackendAddMetadataRecordNullInputStreamThrowsNullPointerException
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|expectedEx
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedEx
operator|.
name|expectMessage
argument_list|(
literal|"input should not be null"
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
operator|(
name|InputStream
operator|)
literal|null
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBackendAddMetadataRecordNullFileThrowsNullPointerException
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|expectedEx
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedEx
operator|.
name|expectMessage
argument_list|(
literal|"input should not be null"
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
operator|(
name|File
operator|)
literal|null
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBackendAddMetadataRecordNullEmptyNameThrowsIllegalArgumentException
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
specifier|final
name|String
name|data
init|=
literal|"testData"
decl_stmt|;
for|for
control|(
name|boolean
name|fromInputStream
range|:
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
control|)
block|{
for|for
control|(
name|String
name|name
range|:
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|)
control|)
block|{
try|try
block|{
if|if
condition|(
name|fromInputStream
condition|)
block|{
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|File
name|testFile
init|=
name|folder
operator|.
name|newFile
argument_list|()
decl_stmt|;
name|copyInputStreamToFile
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|testFile
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|testFile
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"name should not be empty"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// GetMetadataRecord (Backend)
annotation|@
name|Test
specifier|public
name|void
name|testBackendGetMetadataRecordInvalidName
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"testRecord"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
control|)
block|{
try|try
block|{
name|s3ds
operator|.
name|getMetadataRecord
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expect to throw"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
block|}
name|s3ds
operator|.
name|deleteMetadataRecord
argument_list|(
literal|"testRecord"
argument_list|)
expr_stmt|;
block|}
comment|// GetAllMetadataRecords (Backend)
annotation|@
name|Test
specifier|public
name|void
name|testBackendGetAllMetadataRecordsPrefixMatchesAll
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
literal|""
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|prefixAll
init|=
literal|"prefix1"
decl_stmt|;
name|String
name|prefixSome
init|=
literal|"prefix1.prefix2"
decl_stmt|;
name|String
name|prefixOne
init|=
literal|"prefix1.prefix3"
decl_stmt|;
name|String
name|prefixNone
init|=
literal|"prefix4"
decl_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%s.testRecord1"
argument_list|,
name|prefixAll
argument_list|)
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%s.testRecord2"
argument_list|,
name|prefixSome
argument_list|)
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%s.testRecord3"
argument_list|,
name|prefixSome
argument_list|)
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|4
argument_list|,
literal|10
argument_list|)
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%s.testRecord4"
argument_list|,
name|prefixOne
argument_list|)
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"prefix5.testRecord5"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
literal|""
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
name|prefixAll
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
name|prefixSome
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
name|prefixOne
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
name|prefixNone
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|deleteAllMetadataRecords
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
literal|""
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBackendGetAllMetadataRecordsNullPrefixThrowsNullPointerException
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|expectedEx
operator|.
name|expect
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|)
expr_stmt|;
name|expectedEx
operator|.
name|expectMessage
argument_list|(
literal|"prefix should not be null"
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// MetadataRecordExists (Backend)
annotation|@
name|Test
specifier|public
name|void
name|testBackendMetadataRecordExists
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"invalid"
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
control|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|name
argument_list|)
condition|)
block|{
try|try
block|{
name|s3ds
operator|.
name|metadataRecordExists
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{ }
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|s3ds
operator|.
name|metadataRecordExists
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|s3ds
operator|.
name|metadataRecordExists
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// DeleteMetadataRecord (Backend)
annotation|@
name|Test
specifier|public
name|void
name|testBackendDeleteMetadataRecord
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|)
control|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|name
argument_list|)
condition|)
block|{
try|try
block|{
name|s3ds
operator|.
name|deleteMetadataRecord
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{ }
block|}
else|else
block|{
name|s3ds
operator|.
name|deleteMetadataRecord
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|s3ds
operator|.
name|deleteMetadataRecord
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// DeleteAllMetadataRecords (Backend)
annotation|@
name|Test
specifier|public
name|void
name|testBackendDeleteAllMetadataRecordsPrefixMatchesAll
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
name|String
name|prefixAll
init|=
literal|"prefix1"
decl_stmt|;
name|String
name|prefixSome
init|=
literal|"prefix1.prefix2"
decl_stmt|;
name|String
name|prefixOne
init|=
literal|"prefix1.prefix3"
decl_stmt|;
name|String
name|prefixNone
init|=
literal|"prefix4"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|prefixCounts
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|prefixCounts
operator|.
name|put
argument_list|(
name|prefixAll
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|prefixCounts
operator|.
name|put
argument_list|(
name|prefixSome
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|prefixCounts
operator|.
name|put
argument_list|(
name|prefixOne
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|prefixCounts
operator|.
name|put
argument_list|(
name|prefixNone
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|prefixCounts
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%s.testRecord1"
argument_list|,
name|prefixAll
argument_list|)
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%s.testRecord2"
argument_list|,
name|prefixSome
argument_list|)
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%s.testRecord3"
argument_list|,
name|prefixSome
argument_list|)
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|addMetadataRecord
argument_list|(
name|randomStream
argument_list|(
literal|4
argument_list|,
literal|10
argument_list|)
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%s.testRecord4"
argument_list|,
name|prefixOne
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|preCount
init|=
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
literal|""
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|s3ds
operator|.
name|deleteAllMetadataRecords
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|deletedCount
init|=
name|preCount
operator|-
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
literal|""
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|,
name|deletedCount
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|deleteAllMetadataRecords
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBackendDeleteAllMetadataRecordsNoRecordsNoChange
parameter_list|()
throws|throws
name|Exception
block|{
name|assumeTrue
argument_list|(
name|isS3Configured
argument_list|()
argument_list|)
expr_stmt|;
name|S3DataStore
name|s3ds
init|=
name|getDataStore
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
literal|""
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|s3ds
operator|.
name|deleteAllMetadataRecords
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|s3ds
operator|.
name|getAllMetadataRecords
argument_list|(
literal|""
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|S3DataStore
name|getDataStore
parameter_list|()
throws|throws
name|Exception
block|{
name|props
operator|=
name|S3DataStoreUtils
operator|.
name|getS3Config
argument_list|()
expr_stmt|;
name|bucket
operator|=
name|props
operator|.
name|getProperty
argument_list|(
name|S3Constants
operator|.
name|S3_BUCKET
argument_list|)
expr_stmt|;
name|bucket
operator|=
name|bucket
operator|+
literal|"-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|props
operator|.
name|put
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
name|put
argument_list|(
literal|"cacheSize"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
return|return
operator|(
name|S3DataStore
operator|)
name|getS3DataStore
argument_list|(
name|s3Class
argument_list|,
name|props
argument_list|,
name|dataStoreDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|bucket
operator|!=
literal|null
condition|)
block|{
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
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{         }
block|}
block|}
end_class

end_unit

