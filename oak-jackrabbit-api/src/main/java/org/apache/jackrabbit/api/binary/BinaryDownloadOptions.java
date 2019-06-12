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
name|api
operator|.
name|binary
package|;
end_package

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
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * Specifies the options to be used when obtaining a direct download URI via  * {@link BinaryDownload#getURI(BinaryDownloadOptions)}.  Setting these options  * allows the caller to instruct the service provider that these options should  * be applied to the response to a request made with the URI returned.  *<p>  * To specify download options, obtain a {@link BinaryDownloadOptionsBuilder}  * via the {@link #builder()} method, then specify the options desired and  * get the object via {@link BinaryDownloadOptionsBuilder#build()}.  *<p>  * If no options are needed, use {@link BinaryDownloadOptions#DEFAULT} which  * instructs the implementation to use the service provider default behavior.  */
end_comment

begin_class
annotation|@
name|ProviderType
specifier|public
specifier|final
class|class
name|BinaryDownloadOptions
block|{
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
name|BinaryDownloadOptions
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
name|dispositionType
expr_stmt|;
block|}
comment|/**      * Provides a default instance of this class.  Using the default instance      * indicates that the caller is willing to accept the service provider      * default behavior.      */
specifier|public
specifier|static
specifier|final
name|BinaryDownloadOptions
name|DEFAULT
init|=
name|BinaryDownloadOptions
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/**      * Returns the internet media type that should be assumed for the binary that is to be      * downloaded.  This value should be a valid {@code jcr:mimeType}.  This      * value can be set by calling {@link      * BinaryDownloadOptionsBuilder#withMediaType(String)} when building an      * instance of this class.      *      * @return A String representation of the internet media type, or {@code null} if no      *         type has been specified.      * @see<a href="https://docs.adobe.com/content/docs/en/spec/jcr/2.0/3_Repository_Model.html#3.7.11.10%20mix:mimeType">      *     JCR 2.0 Repository Model - jcr:mimeType</a>      */
annotation|@
name|Nullable
specifier|public
specifier|final
name|String
name|getMediaType
parameter_list|()
block|{
return|return
name|mediaType
return|;
block|}
comment|/**      * Returns the character encoding that should be assumed for the binary that      * is to be downloaded.  This value should be a valid {@code jcr:encoding}.      * It can be set by calling {@link      * BinaryDownloadOptionsBuilder#withCharacterEncoding(String)} when building an      * instance of this class.      *      * @return The character encoding, or {@code null} if no      *         encoding has been specified.      * @see<a href="https://docs.adobe.com/content/docs/en/spec/jcr/2.0/3_Repository_Model.html#3.7.11.10%20mix:mimeType">      *     JCR 2.0 Repository Model - jcr:encoding</a>      */
annotation|@
name|Nullable
specifier|public
specifier|final
name|String
name|getCharacterEncoding
parameter_list|()
block|{
return|return
name|characterEncoding
return|;
block|}
comment|/**      * Returns the filename that should be assumed for the binary that is to be      * downloaded.  This value can be set by calling {@link      * BinaryDownloadOptionsBuilder#withFileName(String)} when building an      * instance of this class.      *      * @return The file name, or {@code null} if no      * file name has been specified.      */
annotation|@
name|Nullable
specifier|public
specifier|final
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|fileName
return|;
block|}
comment|/**      * Returns the disposition type that should be assumed for the binary that      * is to be downloaded.  This value can be set by calling {@link      * BinaryDownloadOptionsBuilder#withDispositionTypeInline()} or {@link      * BinaryDownloadOptionsBuilder#withDispositionTypeAttachment()} when      * building an instance of this class.  The default value of this setting is      * "inline".      *      * @return The disposition type.      * @see<a href="https://tools.ietf.org/html/rfc6266#section-4.2">RFC 6266, Section 4.2</a>       */
annotation|@
name|NotNull
specifier|public
specifier|final
name|String
name|getDispositionType
parameter_list|()
block|{
return|return
name|dispositionType
return|;
block|}
comment|/**      * Returns a {@link BinaryDownloadOptionsBuilder} instance to be used for      * creating an instance of this class.      *      * @return A builder instance.      */
annotation|@
name|NotNull
specifier|public
specifier|static
name|BinaryDownloadOptionsBuilder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|BinaryDownloadOptionsBuilder
argument_list|()
return|;
block|}
comment|/**      * Used to build an instance of {@link BinaryDownloadOptions} with the      * options set as desired by the caller.      */
specifier|public
specifier|static
specifier|final
class|class
name|BinaryDownloadOptionsBuilder
block|{
specifier|private
name|String
name|mediaType
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|characterEncoding
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|fileName
init|=
literal|null
decl_stmt|;
specifier|private
name|DispositionType
name|dispositionType
init|=
name|DispositionType
operator|.
name|INLINE
decl_stmt|;
specifier|private
name|BinaryDownloadOptionsBuilder
parameter_list|()
block|{ }
comment|/**          * Sets the internet media type of the {@link BinaryDownloadOptions} object to be          * built.  This value should be a valid {@code jcr:mimeType}.          *<p>          * Calling this method has the effect of instructing the service          * provider to set {@code mediaType} as the internet media type          * in the {@code Content-Type} header field of the response to a request          * issued with a URI obtained by calling {@link          * BinaryDownload#getURI(BinaryDownloadOptions)}.  This value can be          * later retrieved by calling {@link          * BinaryDownloadOptions#getMediaType()} on the instance returned from a          * call to {@link #build()}.          *<p>          * Note that if the internet media type defines a "charset" parameter          * (as many textual types do), the caller may also wish to set the          * character encoding which is done separately.  See {@link          * #withCharacterEncoding(String)}.          *<p>          * The caller should ensure that the internet media type set is valid; the          * implementation does not perform any validation of this setting.          *<p>          * If no internet media type is provided, no {@code Content-Type} header field will be          * specified to the service provider.          *          * @param mediaType The internet media type.          * @return The calling instance.          * @see<a href="https://docs.adobe.com/content/docs/en/spec/jcr/2.0/3_Repository_Model.html#3.7.11.10%20mix:mimeType">          *     JCR 2.0 Repository Model - jcr:mimeType</a>          */
annotation|@
name|NotNull
specifier|public
name|BinaryDownloadOptionsBuilder
name|withMediaType
parameter_list|(
annotation|@
name|NotNull
name|String
name|mediaType
parameter_list|)
block|{
name|this
operator|.
name|mediaType
operator|=
name|mediaType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the character encoding of the {@link BinaryDownloadOptions} object to be          * built. This value should be a valid {@code jcr:encoding} property value.          *<p>          * Calling this method has the effect of instructing the service          * provider to set {@code characterEncoding} as the "charset" parameter          * of the content type in the {@code Content-Type} header field of the          * response to a request issued with a URI obtained by calling {@link          * BinaryDownload#getURI(BinaryDownloadOptions)}.  This value can be          * later retrieved by calling {@link          * BinaryDownloadOptions#getCharacterEncoding()} on the instance returned by a          * call to {@link #build()}.          *<p>          * Note that setting the character encoding only makes sense if the internet          * media type has also been set, and that media type actually defines a          * "charset" parameter. See {@link #withMediaType(String)}.          *<p>          * The caller should ensure that the proper character encoding has been set for          * the internet media type; the implementation does not perform any validation of          * these settings.          *          * @param characterEncoding A String representation of the jcr:encoding.          * @return The calling instance.          * @see<a href="https://docs.adobe.com/content/docs/en/spec/jcr/2.0/3_Repository_Model.html#3.7.11.10%20mix:mimeType">          *     JCR 2.0 Repository Model - jcr:encoding</a>          */
annotation|@
name|NotNull
specifier|public
name|BinaryDownloadOptionsBuilder
name|withCharacterEncoding
parameter_list|(
annotation|@
name|NotNull
name|String
name|characterEncoding
parameter_list|)
block|{
name|this
operator|.
name|characterEncoding
operator|=
name|characterEncoding
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the filename of the {@link BinaryDownloadOptions} object to be          * built. This would typically be based on a JCR node name.          *<p>          * Calling this method has the effect of instructing the service          * provider to set {@code fileName} as the filename in the {@code          * Content-Disposition} header of the response to a request issued with          * a URI obtained by calling {@link          * BinaryDownload#getURI(BinaryDownloadOptions)}.  This value can be          * later retrieved by calling {@link          * BinaryDownloadOptions#getFileName()} on the instance returned by a          * call to {@link #build()}.          *<p>          *          * @param fileName The filename.          * @return The calling instance.          * @see<a href="https://tools.ietf.org/html/rfc6266#section-4.3">RFC 6266, Section 4.3</a>           */
annotation|@
name|NotNull
specifier|public
name|BinaryDownloadOptionsBuilder
name|withFileName
parameter_list|(
annotation|@
name|NotNull
name|String
name|fileName
parameter_list|)
block|{
name|this
operator|.
name|fileName
operator|=
name|fileName
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the disposition type of the {@link BinaryDownloadOptions} object          * to be built to {@code inline}.          *<p>          * Calling this method has the effect of instructing the service          * provider to set the disposition type in the {@code          * Content-Disposition} header of the response to {@code inline}.  This          * value can be later retrieved by calling {@link          * BinaryDownloadOptions#getDispositionType()} on the instance built by          * calling {@link #build()}.          *<p>          * If this value is not set, the default value of {@code inline}          * will be used.          *          * @return The calling instance.          */
annotation|@
name|NotNull
specifier|public
name|BinaryDownloadOptionsBuilder
name|withDispositionTypeInline
parameter_list|()
block|{
name|dispositionType
operator|=
name|DispositionType
operator|.
name|INLINE
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Sets the disposition type of the {@link BinaryDownloadOptions} object          * to be built to {@code attachment}.          *<p>          * Calling this method has the effect of instructing the service          * provider to set the disposition type in the {@code          * Content-Disposition} header of the response to {@code attachment}.          * This value can later be retrieved by calling {@link          * BinaryDownloadOptions#getDispositionType()} on the instance built by          * calling {@link #build()}.          *<p>          * If this value is not set, the default value of {@code inline}          * will be used.          *          * @return The calling instance.          */
annotation|@
name|NotNull
specifier|public
name|BinaryDownloadOptionsBuilder
name|withDispositionTypeAttachment
parameter_list|()
block|{
name|dispositionType
operator|=
name|DispositionType
operator|.
name|ATTACHMENT
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Construct a {@link BinaryDownloadOptions} instance with the          * properties specified to the builder.          *          * @return A new {@link BinaryDownloadOptions} instance built with the          *         properties specified to the builder.          */
annotation|@
name|NotNull
specifier|public
name|BinaryDownloadOptions
name|build
parameter_list|()
block|{
return|return
operator|new
name|BinaryDownloadOptions
argument_list|(
name|mediaType
argument_list|,
name|characterEncoding
argument_list|,
name|fileName
argument_list|,
literal|null
operator|!=
name|dispositionType
condition|?
name|dispositionType
operator|.
name|toString
argument_list|()
else|:
name|DispositionType
operator|.
name|INLINE
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|private
enum|enum
name|DispositionType
block|{
name|INLINE
argument_list|(
literal|"inline"
argument_list|)
block|,
name|ATTACHMENT
argument_list|(
literal|"attachment"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
name|DispositionType
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

