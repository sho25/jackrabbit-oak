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
operator|.
name|util
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
name|assertNotNull
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
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
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneOffset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
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
name|Node
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
name|Session
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
name|JcrConstants
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
name|azure
operator|.
name|blobstorage
operator|.
name|AzureDataStore
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
name|commons
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
name|directaccess
operator|.
name|ConfigurableDataRecordAccessProvider
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

begin_comment
comment|/** Utility methods to test Binary direct HTTP access */
end_comment

begin_class
specifier|public
class|class
name|BinaryAccessTestUtils
block|{
comment|/** Creates an nt:file based on the content, saves the session and retrieves the Binary value again. */
specifier|public
specifier|static
name|Binary
name|storeBinaryAndRetrieve
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|path
parameter_list|,
name|Content
name|content
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Binary
name|binary
init|=
name|session
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createBinary
argument_list|(
name|content
operator|.
name|getStream
argument_list|()
argument_list|)
decl_stmt|;
name|storeBinary
argument_list|(
name|session
argument_list|,
name|path
argument_list|,
name|binary
argument_list|)
expr_stmt|;
return|return
name|getBinary
argument_list|(
name|session
argument_list|,
name|path
argument_list|)
return|;
block|}
comment|/** Creates an nt:file with a binary at the given path and saves the session. */
specifier|public
specifier|static
name|void
name|storeBinary
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|path
parameter_list|,
name|Binary
name|binary
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|putBinary
argument_list|(
name|session
argument_list|,
name|path
argument_list|,
name|binary
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
comment|/** Creates an nt:file with a binary at the given path. Does not save the session. */
specifier|public
specifier|static
name|void
name|putBinary
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|ntFilePath
parameter_list|,
name|Binary
name|binary
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|ntResource
decl_stmt|;
if|if
condition|(
name|session
operator|.
name|nodeExists
argument_list|(
name|ntFilePath
operator|+
literal|"/"
operator|+
name|JcrConstants
operator|.
name|JCR_CONTENT
argument_list|)
condition|)
block|{
name|ntResource
operator|=
name|session
operator|.
name|getNode
argument_list|(
name|ntFilePath
operator|+
literal|"/"
operator|+
name|JcrConstants
operator|.
name|JCR_CONTENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Node
name|file
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|ntFilePath
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|JcrConstants
operator|.
name|NT_FILE
argument_list|)
decl_stmt|;
name|ntResource
operator|=
name|file
operator|.
name|addNode
argument_list|(
name|JcrConstants
operator|.
name|JCR_CONTENT
argument_list|,
name|JcrConstants
operator|.
name|NT_RESOURCE
argument_list|)
expr_stmt|;
block|}
name|ntResource
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_DATA
argument_list|,
name|binary
argument_list|)
expr_stmt|;
block|}
comment|/** Retrieves the Binary from the jcr:data of an nt:file */
specifier|public
specifier|static
name|Binary
name|getBinary
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|ntFilePath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|session
operator|.
name|getNode
argument_list|(
name|ntFilePath
argument_list|)
operator|.
name|getNode
argument_list|(
name|JcrConstants
operator|.
name|JCR_CONTENT
argument_list|)
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_DATA
argument_list|)
operator|.
name|getBinary
argument_list|()
return|;
block|}
comment|/**      * Uploads data via HTTP put to the provided URI.      *      * @param uri The URI to upload to.      * @param contentLength Value to set in the Content-Length header.      * @param in - The input stream to upload.      * @return HTTP response code from the upload request.  Note that a successful      * response for S3 is 200 - OK whereas for Azure it is 201 - Created.      * @throws IOException      */
specifier|public
specifier|static
name|int
name|httpPut
parameter_list|(
annotation|@
name|Nullable
name|URI
name|uri
parameter_list|,
name|long
name|contentLength
parameter_list|,
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
comment|// this weird combination of @Nullable and assertNotNull() is for IDEs not warning in test methods
name|assertNotNull
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|uri
operator|.
name|toURL
argument_list|()
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"PUT"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setRequestProperty
argument_list|(
literal|"Content-Length"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|contentLength
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setRequestProperty
argument_list|(
literal|"Date"
argument_list|,
name|DateTimeFormatter
operator|.
name|ofPattern
argument_list|(
literal|"yyyy-MM-dd'T'HH:mm:ssX"
argument_list|)
operator|.
name|withZone
argument_list|(
name|ZoneOffset
operator|.
name|UTC
argument_list|)
operator|.
name|format
argument_list|(
name|Instant
operator|.
name|now
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|OutputStream
name|putStream
init|=
name|connection
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|in
argument_list|,
name|putStream
argument_list|)
expr_stmt|;
name|putStream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|connection
operator|.
name|getResponseCode
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isSuccessfulHttpPut
parameter_list|(
name|int
name|code
parameter_list|,
name|ConfigurableDataRecordAccessProvider
name|dataStore
parameter_list|)
block|{
if|if
condition|(
name|dataStore
operator|instanceof
name|S3DataStore
condition|)
block|{
return|return
literal|200
operator|==
name|code
return|;
block|}
elseif|else
if|if
condition|(
name|dataStore
operator|instanceof
name|AzureDataStore
condition|)
block|{
return|return
literal|201
operator|==
name|code
return|;
block|}
return|return
literal|200
operator|==
name|code
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isFailedHttpPut
parameter_list|(
name|int
name|code
parameter_list|)
block|{
return|return
name|code
operator|>=
literal|400
operator|&&
name|code
operator|<
literal|500
return|;
block|}
specifier|public
specifier|static
name|InputStream
name|httpGet
parameter_list|(
annotation|@
name|Nullable
name|URI
name|uri
parameter_list|)
throws|throws
name|IOException
block|{
comment|// this weird combination of @Nullable and assertNotNull() is for IDEs not warning in test methods
name|assertNotNull
argument_list|(
literal|"HTTP download URI is null"
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|uri
operator|.
name|toURL
argument_list|()
operator|.
name|openConnection
argument_list|()
decl_stmt|;
return|return
name|conn
operator|.
name|getInputStream
argument_list|()
return|;
block|}
block|}
end_class

end_unit
