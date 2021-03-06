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
name|jcr
operator|.
name|binary
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Binary
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
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
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|ObservationManager
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
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|api
operator|.
name|JackrabbitSession
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
name|api
operator|.
name|JackrabbitValueFactory
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
name|api
operator|.
name|binary
operator|.
name|BinaryDownload
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
name|api
operator|.
name|binary
operator|.
name|BinaryDownloadOptions
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
name|api
operator|.
name|binary
operator|.
name|BinaryUpload
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
name|api
operator|.
name|binary
operator|.
name|BinaryUploadOptions
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Authorizable
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|UserManager
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
name|Oak
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
name|api
operator|.
name|Blob
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
name|api
operator|.
name|blob
operator|.
name|BlobAccessProvider
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
name|api
operator|.
name|blob
operator|.
name|BlobDownloadOptions
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
name|api
operator|.
name|blob
operator|.
name|BlobUpload
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
name|api
operator|.
name|blob
operator|.
name|BlobUploadOptions
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
name|fixture
operator|.
name|NodeStoreFixture
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
name|jcr
operator|.
name|AbstractRepositoryTest
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
name|jcr
operator|.
name|Jcr
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
name|jcr
operator|.
name|binary
operator|.
name|fixtures
operator|.
name|datastore
operator|.
name|FileDataStoreFixture
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
name|jcr
operator|.
name|binary
operator|.
name|fixtures
operator|.
name|nodestore
operator|.
name|SegmentMemoryNodeStoreFixture
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
name|jcr
operator|.
name|binary
operator|.
name|util
operator|.
name|BinaryAccessTestUtils
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
name|jcr
operator|.
name|binary
operator|.
name|util
operator|.
name|Content
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|test
operator|.
name|LogPrintWriter
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
name|test
operator|.
name|api
operator|.
name|observation
operator|.
name|EventResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
comment|/**  * This is a unit test for the direct binary access JCR API extension.  * It uses a mock of the underlying BlobAccessProvider.  *  * For a full integration test against real binary cloud storage,  * see BinaryAccessIt.  */
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
name|BinaryAccessTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BinaryAccessTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|Iterable
argument_list|<
name|?
argument_list|>
name|dataStoreFixtures
parameter_list|()
block|{
name|Collection
argument_list|<
name|NodeStoreFixture
argument_list|>
name|fixtures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|fixtures
operator|.
name|add
argument_list|(
operator|new
name|SegmentMemoryNodeStoreFixture
argument_list|(
operator|new
name|FileDataStoreFixture
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|fixtures
return|;
block|}
specifier|public
name|BinaryAccessTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|String
name|FILE_PATH
init|=
literal|"/file"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|SEGMENT_INLINE_SIZE
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DOWNLOAD_URL
init|=
literal|"http://expected.com/dummy/url/for/test/download"
decl_stmt|;
specifier|private
specifier|static
name|URI
name|expectedDownloadURI
parameter_list|()
block|{
return|return
name|toURI
argument_list|(
name|DOWNLOAD_URL
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|DEFAULT_CDN_DOWNLOAD_URL
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|String
name|ALT_CDN_DOWNLOAD_URL
init|=
literal|"http://cdn.com/dumm/url/for/test/download"
decl_stmt|;
specifier|private
specifier|static
name|String
name|CDN_DOWNLOAD_URL
init|=
name|DEFAULT_CDN_DOWNLOAD_URL
decl_stmt|;
specifier|private
specifier|static
name|URI
name|cdnDownloadURI
parameter_list|()
block|{
return|return
name|CDN_DOWNLOAD_URL
operator|==
literal|null
condition|?
literal|null
else|:
name|toURI
argument_list|(
name|CDN_DOWNLOAD_URL
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|String
name|UPLOAD_TOKEN
init|=
literal|"super-safe-encrypted-token"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|UPLOAD_URL
init|=
literal|"http://expected.com/dummy/url/for/test/upload"
decl_stmt|;
specifier|private
specifier|static
name|URI
name|expectedUploadURI
parameter_list|()
block|{
return|return
name|toURI
argument_list|(
name|UPLOAD_URL
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|DEFAULT_CDN_UPLOAD_URL
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|String
name|ALT_CDN_UPLOAD_URL
init|=
literal|"http://cdn.com/dummy/url/for/test/upload"
decl_stmt|;
specifier|private
specifier|static
name|String
name|CDN_UPLOAD_URL
init|=
name|DEFAULT_CDN_UPLOAD_URL
decl_stmt|;
specifier|private
specifier|static
name|URI
name|cdnUploadURI
parameter_list|()
block|{
return|return
name|CDN_UPLOAD_URL
operator|==
literal|null
condition|?
literal|null
else|:
name|toURI
argument_list|(
name|CDN_UPLOAD_URL
argument_list|)
return|;
block|}
comment|// These tests need to run sync - the lock will force that even if the user tries to run them in parallel
specifier|private
specifier|static
specifier|final
name|Lock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|URI
name|toURI
parameter_list|(
name|String
name|url
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|URI
argument_list|(
name|url
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|Content
name|blobContent
decl_stmt|;
comment|/**      * Adjust JCR repository creation to register a mock BlobAccessProvider in Whiteboard      * so it can be picked up by oak-jcr.      */
annotation|@
name|Override
specifier|protected
name|Repository
name|createRepository
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
name|oak
operator|.
name|getWhiteboard
argument_list|()
operator|.
name|register
argument_list|(
name|BlobAccessProvider
operator|.
name|class
argument_list|,
operator|new
name|MockBlobAccessProvider
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|initJcr
argument_list|(
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
argument_list|)
operator|.
name|createRepository
argument_list|()
return|;
block|}
specifier|private
class|class
name|MockBlobAccessProvider
implements|implements
name|BlobAccessProvider
block|{
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|BlobUpload
name|initiateBlobUpload
parameter_list|(
name|long
name|maxSize
parameter_list|,
name|int
name|maxURIs
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
return|return
name|initiateBlobUpload
argument_list|(
name|maxSize
argument_list|,
name|maxURIs
argument_list|,
name|BlobUploadOptions
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|BlobUpload
name|initiateBlobUpload
parameter_list|(
name|long
name|maxSize
parameter_list|,
name|int
name|maxURIs
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|BlobUploadOptions
name|options
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
return|return
operator|new
name|BlobUpload
argument_list|()
block|{
annotation|@
name|Override
specifier|public
annotation|@
name|NotNull
name|String
name|getUploadToken
parameter_list|()
block|{
return|return
name|UPLOAD_TOKEN
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMinPartSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMaxPartSize
parameter_list|()
block|{
return|return
literal|10
operator|*
literal|1024
operator|*
literal|1024
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|NotNull
name|Collection
argument_list|<
name|URI
argument_list|>
name|getUploadURIs
parameter_list|()
block|{
name|Collection
argument_list|<
name|URI
argument_list|>
name|uris
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|URI
name|uri
init|=
name|cdnUploadURI
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|uri
operator|||
name|options
operator|.
name|isDomainOverrideIgnored
argument_list|()
condition|)
block|{
name|uri
operator|=
name|expectedUploadURI
argument_list|()
expr_stmt|;
block|}
name|uris
operator|.
name|add
argument_list|(
name|uri
argument_list|)
expr_stmt|;
return|return
name|uris
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|Blob
name|completeBlobUpload
parameter_list|(
annotation|@
name|NotNull
name|String
name|uploadToken
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
operator|!
name|UPLOAD_TOKEN
operator|.
name|equals
argument_list|(
name|uploadToken
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// this returns the binary content set on the "blobContent" member
comment|// as a simple way to mock some binary "storage"
return|return
operator|new
name|Blob
argument_list|()
block|{
annotation|@
name|Override
specifier|public
annotation|@
name|NotNull
name|InputStream
name|getNewStream
parameter_list|()
block|{
return|return
name|blobContent
operator|.
name|getStream
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|blobContent
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReference
parameter_list|()
block|{
return|return
literal|"super-secure-key#"
operator|+
name|getContentIdentity
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentIdentity
parameter_list|()
block|{
return|return
name|DigestUtils
operator|.
name|md5Hex
argument_list|(
name|blobContent
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|URI
name|getDownloadURI
parameter_list|(
annotation|@
name|NotNull
name|Blob
name|blob
parameter_list|,
annotation|@
name|NotNull
name|BlobDownloadOptions
name|blobDownloadOptions
parameter_list|)
block|{
name|URI
name|uri
init|=
name|cdnDownloadURI
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|uri
operator|||
name|blobDownloadOptions
operator|.
name|isDomainOverrideIgnored
argument_list|()
condition|)
block|{
name|uri
operator|=
name|expectedDownloadURI
argument_list|()
expr_stmt|;
block|}
return|return
name|uri
return|;
block|}
block|}
specifier|private
name|BinaryDownload
name|setupBinaryDownloadTest
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Content
name|content
init|=
name|Content
operator|.
name|createRandom
argument_list|(
name|SEGMENT_INLINE_SIZE
operator|*
literal|2
argument_list|)
decl_stmt|;
name|Binary
name|binary
init|=
name|BinaryAccessTestUtils
operator|.
name|storeBinaryAndRetrieve
argument_list|(
name|getAdminSession
argument_list|()
argument_list|,
name|FILE_PATH
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|binary
operator|instanceof
name|BinaryDownload
argument_list|)
expr_stmt|;
return|return
operator|(
name|BinaryDownload
operator|)
name|binary
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBinaryDownload
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|BinaryDownload
name|binaryDownload
init|=
name|setupBinaryDownloadTest
argument_list|()
decl_stmt|;
name|URI
name|uri
init|=
name|binaryDownload
operator|.
name|getURI
argument_list|(
name|BinaryDownloadOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
comment|// we only need test that the we get a URI back (from our mock) to validate oak-jcr's inner workings
name|assertNotNull
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedDownloadURI
argument_list|()
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBinaryDownloadWithCDN
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|BinaryDownload
name|binaryDownload
init|=
name|setupBinaryDownloadTest
argument_list|()
decl_stmt|;
name|String
name|cdnUrlBefore
init|=
name|CDN_DOWNLOAD_URL
decl_stmt|;
name|URI
name|uri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CDN_DOWNLOAD_URL
operator|=
name|ALT_CDN_DOWNLOAD_URL
expr_stmt|;
name|uri
operator|=
name|binaryDownload
operator|.
name|getURI
argument_list|(
name|BinaryDownloadOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|CDN_DOWNLOAD_URL
operator|=
name|cdnUrlBefore
expr_stmt|;
block|}
comment|// we only need test that the we get a URI back (from our mock) to validate oak-jcr's inner workings
name|assertNotNull
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|toURI
argument_list|(
name|ALT_CDN_DOWNLOAD_URL
argument_list|)
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBinaryDownloadWithCDNIgnored
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|BinaryDownload
name|binaryDownload
init|=
name|setupBinaryDownloadTest
argument_list|()
decl_stmt|;
name|String
name|cdnUrlBefore
init|=
name|CDN_DOWNLOAD_URL
decl_stmt|;
name|URI
name|uri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CDN_DOWNLOAD_URL
operator|=
name|ALT_CDN_DOWNLOAD_URL
expr_stmt|;
name|uri
operator|=
name|binaryDownload
operator|.
name|getURI
argument_list|(
name|BinaryDownloadOptions
operator|.
name|builder
argument_list|()
operator|.
name|withDomainOverrideIgnored
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|CDN_DOWNLOAD_URL
operator|=
name|cdnUrlBefore
expr_stmt|;
block|}
comment|// we only need test that the we get a URI back (from our mock) to validate oak-jcr's inner workings
name|assertNotNull
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedDownloadURI
argument_list|()
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBinaryUpload
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|IOException
block|{
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|Content
name|content
init|=
name|Content
operator|.
name|createRandom
argument_list|(
name|SEGMENT_INLINE_SIZE
operator|*
literal|2
argument_list|)
decl_stmt|;
name|ValueFactory
name|vf
init|=
name|getAdminSession
argument_list|()
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|vf
operator|instanceof
name|JackrabbitValueFactory
argument_list|)
expr_stmt|;
name|JackrabbitValueFactory
name|valueFactory
init|=
operator|(
name|JackrabbitValueFactory
operator|)
name|vf
decl_stmt|;
comment|// 1. test initiate
name|BinaryUpload
name|binaryUpload
init|=
name|valueFactory
operator|.
name|initiateBinaryUpload
argument_list|(
name|content
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|binaryUpload
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|UPLOAD_TOKEN
argument_list|,
name|binaryUpload
operator|.
name|getUploadToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedUploadURI
argument_list|()
argument_list|,
name|binaryUpload
operator|.
name|getUploadURIs
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2. simulate an "upload"
name|blobContent
operator|=
name|content
expr_stmt|;
comment|// 3. test complete
name|Binary
name|binary
init|=
name|valueFactory
operator|.
name|completeBinaryUpload
argument_list|(
name|binaryUpload
operator|.
name|getUploadToken
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|binary
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|content
operator|.
name|size
argument_list|()
argument_list|,
name|binary
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// 4. test that we can use this binary in JCR
name|BinaryAccessTestUtils
operator|.
name|storeBinary
argument_list|(
name|getAdminSession
argument_list|()
argument_list|,
name|FILE_PATH
argument_list|,
name|binary
argument_list|)
expr_stmt|;
name|binary
operator|=
name|BinaryAccessTestUtils
operator|.
name|getBinary
argument_list|(
name|getAdminSession
argument_list|()
argument_list|,
name|FILE_PATH
argument_list|)
expr_stmt|;
name|content
operator|.
name|assertEqualsWith
argument_list|(
name|binary
operator|.
name|getStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBinaryUploadWithCDN
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|Content
name|content
init|=
name|Content
operator|.
name|createRandom
argument_list|(
name|SEGMENT_INLINE_SIZE
operator|*
literal|2
argument_list|)
decl_stmt|;
name|String
name|cdnUrlBefore
init|=
name|CDN_UPLOAD_URL
decl_stmt|;
name|BinaryUpload
name|binaryUpload
init|=
literal|null
decl_stmt|;
name|URI
name|uploadUri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CDN_UPLOAD_URL
operator|=
name|ALT_CDN_UPLOAD_URL
expr_stmt|;
name|ValueFactory
name|vf
init|=
name|getAdminSession
argument_list|()
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|vf
operator|instanceof
name|JackrabbitValueFactory
argument_list|)
expr_stmt|;
name|JackrabbitValueFactory
name|valueFactory
init|=
operator|(
name|JackrabbitValueFactory
operator|)
name|vf
decl_stmt|;
name|binaryUpload
operator|=
name|valueFactory
operator|.
name|initiateBinaryUpload
argument_list|(
name|content
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|uploadUri
operator|=
name|binaryUpload
operator|.
name|getUploadURIs
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|CDN_UPLOAD_URL
operator|=
name|cdnUrlBefore
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|binaryUpload
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|uploadUri
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|toURI
argument_list|(
name|ALT_CDN_UPLOAD_URL
argument_list|)
argument_list|,
name|uploadUri
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBinaryUploadWithCDNOverride
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|Content
name|content
init|=
name|Content
operator|.
name|createRandom
argument_list|(
name|SEGMENT_INLINE_SIZE
operator|*
literal|2
argument_list|)
decl_stmt|;
name|String
name|cdnUrlBefore
init|=
name|CDN_UPLOAD_URL
decl_stmt|;
name|BinaryUpload
name|binaryUpload
init|=
literal|null
decl_stmt|;
name|URI
name|uploadUri
init|=
literal|null
decl_stmt|;
try|try
block|{
name|CDN_UPLOAD_URL
operator|=
name|ALT_CDN_UPLOAD_URL
expr_stmt|;
name|ValueFactory
name|vf
init|=
name|getAdminSession
argument_list|()
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|vf
operator|instanceof
name|JackrabbitValueFactory
argument_list|)
expr_stmt|;
name|JackrabbitValueFactory
name|valueFactory
init|=
operator|(
name|JackrabbitValueFactory
operator|)
name|vf
decl_stmt|;
name|binaryUpload
operator|=
name|valueFactory
operator|.
name|initiateBinaryUpload
argument_list|(
name|content
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|,
name|BinaryUploadOptions
operator|.
name|builder
argument_list|()
operator|.
name|withDomainOverrideIgnore
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|uploadUri
operator|=
name|binaryUpload
operator|.
name|getUploadURIs
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|CDN_UPLOAD_URL
operator|=
name|cdnUrlBefore
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|binaryUpload
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|uploadUri
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedUploadURI
argument_list|()
argument_list|,
name|uploadUri
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEvent
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|BinaryAccessTestUtils
operator|.
name|storeBinaryAndRetrieve
argument_list|(
name|getAdminSession
argument_list|()
argument_list|,
name|FILE_PATH
argument_list|,
name|Content
operator|.
name|createRandom
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ObservationManager
name|obsMgr
init|=
name|getAdminSession
argument_list|()
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
decl_stmt|;
name|EventResult
name|result
init|=
operator|new
name|EventResult
argument_list|(
operator|new
name|LogPrintWriter
argument_list|(
name|LOG
argument_list|)
argument_list|)
decl_stmt|;
name|obsMgr
operator|.
name|addEventListener
argument_list|(
name|result
argument_list|,
name|Event
operator|.
name|PROPERTY_CHANGED
argument_list|,
name|FILE_PATH
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Content
name|content
init|=
name|Content
operator|.
name|createRandom
argument_list|(
name|SEGMENT_INLINE_SIZE
operator|*
literal|2
argument_list|)
decl_stmt|;
name|BinaryAccessTestUtils
operator|.
name|storeBinaryAndRetrieve
argument_list|(
name|getAdminSession
argument_list|()
argument_list|,
name|FILE_PATH
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|Event
index|[]
name|events
init|=
name|result
operator|.
name|getEvents
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|events
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Event
operator|.
name|PROPERTY_CHANGED
argument_list|,
name|events
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Value
name|afterValue
init|=
operator|(
name|Value
operator|)
name|events
index|[
literal|0
index|]
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"afterValue"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|afterValue
argument_list|)
expr_stmt|;
name|Binary
name|binary
init|=
name|afterValue
operator|.
name|getBinary
argument_list|()
decl_stmt|;
name|content
operator|.
name|assertEqualsWith
argument_list|(
name|binary
operator|.
name|getStream
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|binary
operator|instanceof
name|BinaryDownload
argument_list|)
expr_stmt|;
name|BinaryDownload
name|binaryDownload
init|=
operator|(
name|BinaryDownload
operator|)
name|binary
decl_stmt|;
name|URI
name|uri
init|=
name|binaryDownload
operator|.
name|getURI
argument_list|(
name|BinaryDownloadOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedDownloadURI
argument_list|()
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAuthorizableProperty
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|getAdminSession
argument_list|()
operator|instanceof
name|JackrabbitSession
argument_list|)
expr_stmt|;
name|JackrabbitSession
name|session
init|=
operator|(
name|JackrabbitSession
operator|)
name|getAdminSession
argument_list|()
decl_stmt|;
name|UserManager
name|userMgr
init|=
name|session
operator|.
name|getUserManager
argument_list|()
decl_stmt|;
name|ValueFactory
name|vf
init|=
name|session
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|Content
name|content
init|=
name|Content
operator|.
name|createRandom
argument_list|(
name|SEGMENT_INLINE_SIZE
operator|*
literal|2
argument_list|)
decl_stmt|;
name|Binary
name|binary
init|=
name|BinaryAccessTestUtils
operator|.
name|storeBinaryAndRetrieve
argument_list|(
name|getAdminSession
argument_list|()
argument_list|,
name|FILE_PATH
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|Authorizable
name|auth
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|session
operator|.
name|getUserID
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|auth
argument_list|)
expr_stmt|;
name|auth
operator|.
name|setProperty
argument_list|(
literal|"avatar"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
name|binary
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|userMgr
operator|.
name|isAutoSave
argument_list|()
condition|)
block|{
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|Value
index|[]
name|values
init|=
name|auth
operator|.
name|getProperty
argument_list|(
literal|"avatar"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|binary
operator|=
name|values
index|[
literal|0
index|]
operator|.
name|getBinary
argument_list|()
expr_stmt|;
name|content
operator|.
name|assertEqualsWith
argument_list|(
name|binary
operator|.
name|getStream
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|binary
operator|instanceof
name|BinaryDownload
argument_list|)
expr_stmt|;
name|BinaryDownload
name|binaryDownload
init|=
operator|(
name|BinaryDownload
operator|)
name|binary
decl_stmt|;
name|URI
name|uri
init|=
name|binaryDownload
operator|.
name|getURI
argument_list|(
name|BinaryDownloadOptions
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedDownloadURI
argument_list|()
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

