package pingcrm.pages

class UserListPage extends BasePage {

    static heading = 'Users'
    static url = '/users'

    static content = {
        userLinks {
            $('table.w-full')
                    .find('a')
        }

        avatarImages(required: false) {
            $('table.w-full')
                    .find('img.rounded-full')
        }

        firstAvatarImage(required: false) {
            avatarImages.firstElement()
        }

        firstAvatarImageSrc(required: false) {
            firstAvatarImage.getDomAttribute('src')
        }

        firstAvatarImageFilename(required: false) {
            firstAvatarImageSrc.substring(
                    firstAvatarImageSrc.lastIndexOf('/')+1,
                    firstAvatarImageSrc.indexOf('?')
            )
        }
    }

    @SuppressWarnings('GrMethodMayBeStatic')
    Long findUserId(String email) {
        var href = userLinks.filter(text: email).attr('href')
        var matcher = href =~ /\/(\d+)\//
        var id = -1
        if(matcher.find()) {
            id = matcher.group(1)
        }
        id as Long
    }
}
