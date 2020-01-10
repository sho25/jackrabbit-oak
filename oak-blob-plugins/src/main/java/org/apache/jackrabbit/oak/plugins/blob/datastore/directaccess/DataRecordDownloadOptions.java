begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|blob
operator|.
name|datastore
operator|.
name|directaccess
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Joiner
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
name|Sets
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

begin_comment
comment|/**  * Contains download options for downloading a data record directly from a  * storage location using the direct download feature.  */
end_comment

begin_class
specifier|public
class|class
name|DataRecordDownloadOptions
block|{
specifier|static
specifier|final
name|String
name|DISPOSITION_TYPE_INLINE
init|=
literal|"inline"
decl_stmt|;
specifier|static
specifier|final
name|String
name|DISPOSITION_TYPE_ATTACHMENT
init|=
literal|"attachment"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|hex
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'A'
block|,
literal|'B'
block|,
literal|'C'
block|,
literal|'D'
block|,
literal|'E'
block|,
literal|'F'
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Character
argument_list|>
name|rfc5987AllowedChars
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|'0'
argument_list|,
literal|'1'
argument_list|,
literal|'2'
argument_list|,
literal|'3'
argument_list|,
literal|'4'
argument_list|,
literal|'5'
argument_list|,
literal|'6'
argument_list|,
literal|'7'
argument_list|,
literal|'8'
argument_list|,
literal|'9'
argument_list|,
literal|'a'
argument_list|,
literal|'b'
argument_list|,
literal|'c'
argument_list|,
literal|'d'
argument_list|,
literal|'e'
argument_list|,
literal|'f'
argument_list|,
literal|'g'
argument_list|,
literal|'h'
argument_list|,
literal|'i'
argument_list|,
literal|'j'
argument_list|,
literal|'k'
argument_list|,
literal|'l'
argument_list|,
literal|'m'
argument_list|,
literal|'n'
argument_list|,
literal|'o'
argument_list|,
literal|'p'
argument_list|,
literal|'q'
argument_list|,
literal|'r'
argument_list|,
literal|'s'
argument_list|,
literal|'t'
argument_list|,
literal|'u'
argument_list|,
literal|'v'
argument_list|,
literal|'w'
argument_list|,
literal|'x'
argument_list|,
literal|'y'
argument_list|,
literal|'z'
argument_list|,
literal|'A'
argument_list|,
literal|'B'
argument_list|,
literal|'C'
argument_list|,
literal|'D'
argument_list|,
literal|'E'
argument_list|,
literal|'F'
argument_list|,
literal|'G'
argument_list|,
literal|'H'
argument_list|,
literal|'I'
argument_list|,
literal|'J'
argument_list|,
literal|'K'
argument_list|,
literal|'L'
argument_list|,
literal|'M'
argument_list|,
literal|'N'
argument_list|,
literal|'O'
argument_list|,
literal|'P'
argument_list|,
literal|'Q'
argument_list|,
literal|'R'
argument_list|,
literal|'S'
argument_list|,
literal|'T'
argument_list|,
literal|'U'
argument_list|,
literal|'V'
argument_list|,
literal|'W'
argument_list|,
literal|'X'
argument_list|,
literal|'Y'
argument_list|,
literal|'Z'
argument_list|,
literal|'!'
argument_list|,
literal|'#'
argument_list|,
literal|'$'
argument_list|,
literal|'&'
argument_list|,
literal|'+'
argument_list|,
literal|'-'
argument_list|,
literal|'.'
argument_list|,
literal|'^'
argument_list|,
literal|'_'
argument_list|,
literal|'`'
argument_list|,
literal|'|'
argument_list|,
literal|'~'
argument_list|)
decl_stmt|;
comment|/**      * Create an instance of this class directly from a {@link      * BlobDownloadOptions} instance.      *      * @param downloadOptions The download options to use to initialize this      *         instance.      * @return The new instance of this class.      */
specifier|public
specifier|static
name|DataRecordDownloadOptions
name|fromBlobDownloadOptions
parameter_list|(
annotation|@
name|NotNull
name|BlobDownloadOptions
name|downloadOptions
parameter_list|)
block|{
return|return
operator|new
name|DataRecordDownloadOptions
argument_list|(
name|downloadOptions
operator|.
name|getMediaType
argument_list|()
argument_list|,
name|downloadOptions
operator|.
name|getCharacterEncoding
argument_list|()
argument_list|,
name|downloadOptions
operator|.
name|getFileName
argument_list|()
argument_list|,
name|downloadOptions
operator|.
name|getDispositionType
argument_list|()
argument_list|,
name|downloadOptions
operator|.
name|isDomainOverrideIgnored
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Provides a default implementation of this class.  Clients should use this      * instance when they have no options to specify and are willing to accept      * the service provider default behavior.      */
specifier|public
specifier|static
name|DataRecordDownloadOptions
name|DEFAULT
init|=
operator|new
name|DataRecordDownloadOptions
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|DISPOSITION_TYPE_INLINE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|mediaType
decl_stmt|;
specifier|private
specifier|final
name|String
name|characterEncoding
decl_stmt|;
specifier|private
specifier|final
name|String
name|fileName
decl_stmt|;
specifier|private
specifier|final
name|String
name|dispositionType
decl_stmt|;
specifier|private
name|boolean
name|domainOverrideIgnored
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|contentTypeHeader
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|contentDispositionHeader
init|=
literal|null
decl_stmt|;
specifier|private
name|DataRecordDownloadOptions
parameter_list|(
specifier|final
name|String
name|mediaType
parameter_list|,
specifier|final
name|String
name|characterEncoding
parameter_list|,
specifier|final
name|String
name|fileName
parameter_list|,
specifier|final
name|String
name|dispositionType
parameter_list|,
name|boolean
name|domainOverrideIgnored
parameter_list|)
block|{
name|this
operator|.
name|mediaType
operator|=
name|mediaType
expr_stmt|;
name|this
operator|.
name|characterEncoding
operator|=
name|characterEncoding
expr_stmt|;
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
name|this
operator|.
name|dispositionType
operator|=
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|dispositionType
argument_list|)
condition|?
name|DISPOSITION_TYPE_INLINE
else|:
name|dispositionType
expr_stmt|;
name|this
operator|.
name|domainOverrideIgnored
operator|=
name|domainOverrideIgnored
expr_stmt|;
block|}
comment|/**      * Generate the correct HTTP {@code Content-Type} header value from the      * {@link #mediaType} and {@link #characterEncoding} in this class, if set.      *<p>      * If {@link #mediaType} has not been given a value, this method will return      * {@code null}.      *      * @return The correct value for a {@code Content-Type} header, or {@code      *         null} if the {@link #mediaType} has not been specified.      */
annotation|@
name|Nullable
specifier|public
name|String
name|getContentTypeHeader
parameter_list|()
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|contentTypeHeader
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|mediaType
argument_list|)
condition|)
block|{
name|contentTypeHeader
operator|=
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|characterEncoding
argument_list|)
condition|?
name|mediaType
else|:
name|Joiner
operator|.
name|on
argument_list|(
literal|"; charset="
argument_list|)
operator|.
name|join
argument_list|(
name|mediaType
argument_list|,
name|characterEncoding
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|contentTypeHeader
return|;
block|}
comment|/**      * Generate the correct HTTP {@code Content-Disposition} header value from      * the {@link #fileName} and {@link #dispositionType} in this class, if set.      *<p>      * A value will be returned if the file name has been set, OR if the      * disposition type has been explicitly set to "attachment".  Otherwise      * {@code null} will be returned.      *      * @return The correct value for a {@code Content-Disposition} header, or      *         {@code null} if the {@link #fileName} has not been specified and      *         the {@link #dispositionType} has not been set to "attachment".      */
annotation|@
name|Nullable
specifier|public
name|String
name|getContentDispositionHeader
parameter_list|()
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|contentDispositionHeader
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
name|String
name|dispositionType
init|=
name|this
operator|.
name|dispositionType
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|dispositionType
argument_list|)
condition|)
block|{
name|dispositionType
operator|=
name|DISPOSITION_TYPE_INLINE
expr_stmt|;
block|}
name|contentDispositionHeader
operator|=
name|formatContentDispositionHeader
argument_list|(
name|dispositionType
argument_list|,
name|fileName
argument_list|,
name|rfc8187Encode
argument_list|(
name|fileName
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DISPOSITION_TYPE_ATTACHMENT
operator|.
name|equals
argument_list|(
name|this
operator|.
name|dispositionType
argument_list|)
condition|)
block|{
name|contentDispositionHeader
operator|=
name|DISPOSITION_TYPE_ATTACHMENT
expr_stmt|;
block|}
block|}
return|return
name|contentDispositionHeader
return|;
block|}
specifier|private
name|String
name|formatContentDispositionHeader
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|String
name|dispositionType
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|String
name|fileName
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|rfc8187EncodedFileName
parameter_list|)
block|{
return|return
literal|null
operator|!=
name|rfc8187EncodedFileName
condition|?
name|String
operator|.
name|format
argument_list|(
literal|"%s; filename=\"%s\"; filename*=UTF-8''%s"
argument_list|,
name|dispositionType
argument_list|,
name|fileName
argument_list|,
name|rfc8187EncodedFileName
argument_list|)
else|:
name|String
operator|.
name|format
argument_list|(
literal|"%s; filename=\"%s\""
argument_list|,
name|dispositionType
argument_list|,
name|fileName
argument_list|)
return|;
block|}
specifier|private
name|String
name|rfc8187Encode
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|String
name|input
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
name|input
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|byte
name|b
range|:
name|bytes
control|)
block|{
name|char
name|c
init|=
operator|(
name|char
operator|)
name|b
decl_stmt|;
if|if
condition|(
name|rfc5987AllowedChars
operator|.
name|contains
argument_list|(
name|c
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'%'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|hex
index|[
literal|0x0F
operator|&
operator|(
name|b
operator|>>>
literal|4
operator|)
index|]
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|hex
index|[
name|b
operator|&
literal|0x0F
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns the media type of this instance.      *      * @return The media type, or {@code null} if it has not been set.      */
annotation|@
name|Nullable
specifier|public
name|String
name|getMediaType
parameter_list|()
block|{
return|return
name|mediaType
return|;
block|}
comment|/**      * Returns the character encoding of this instance.      *      * @return The character encoding, or {@code null} if it has not been set.      */
annotation|@
name|Nullable
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
block|{
return|return
name|characterEncoding
return|;
block|}
comment|/**      * Returns the file name of this instance.      *      * @return The file name, or {@code null} if it has not been set.      */
annotation|@
name|Nullable
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
comment|/**      * Returns the disposition type of this instance.      *      * @return The disposition type, or {@code null} if it has not been set.      */
annotation|@
name|Nullable
specifier|public
name|String
name|getDispositionType
parameter_list|()
block|{
return|return
name|dispositionType
return|;
block|}
comment|/**      * Indicates whether the data store should ignore any configured download      * domain override value when generating the signed download URI.      *      * @return true if the domain override should be ignored; false otherwise.      */
specifier|public
name|boolean
name|isDomainOverrideIgnored
parameter_list|()
block|{
return|return
name|domainOverrideIgnored
return|;
block|}
block|}
end_class

end_unit

