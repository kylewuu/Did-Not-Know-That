# Did Not Know That

The goal of this application is for the users to quickly learn about interesting topics on their Android devices.

The application will fetch and summarize Wikipedia articles and display them to the users as cards. The articles that are chosen are based on what the users like.

The users will train the AI by swiping right to "like" an article, and swiping left to "dislike" an article. From this, the AI will be able to decide on what new articles to fetch and show to the users.

## How it was developed

This is a Native Android Application written in Java and C++. This app also uses a word embedding program written in C++ that can be found [here](https://github.com/kylewuu/Word-Embedding). This word embedding program is added as a Firebase function and can be called by the app.

## How it works

1. The app keeps track of topics that the users like.
2. To find an article, a topic is chosen from the list of user topics and is used to search for an article on Wikipedia.
3. The article is web scraped using jsoup then parsed.
4. The article is then summarized using C++ to just ten sentences.
5. The summarized article is then displayed on a swipeable card.
6. The user can swipe right on the topic to "like" it, and it will be added to the user's likes.
7. More topics are found by using a user liked topic to find more topics. A user-liked topic is passed to a word embedding Firebase Function written in C++ (check it out [here](https://github.com/kylewuu/Word-Embedding)), and will return five more topics, which all relate to the user-liked topic passed in. Since the new words are found based on user-liked topic, the new topics will have a high chance of also being liked by the user.

Other features and notes:

- New topics that are found are not immediately added to the collection of user's likes. It will only be added once the users have swiped right on it, then it can be used to find more topics.
- Users have to pick from a list of twelve words to get started.
- There is a difference between user topics and user-liked topics. User topics consist of user's likes as well as the new words that were found using word embedding. User liked topics are only the topics that the users have swiped right on. Articles are found using user topics.
- By swiping left to "dislike", it is pretty much the equivalent of just not "liking" it. Therefore the app only cares about the liked words, and will just simply ignore the disliked words instead of handling disliked words.
- Swiping left on the starter topic cards will not dislike them, because they have already been added to the user's likes by choosing them in the first initialization screen.

## Try it out now!

Clone this repo into Android Studio and run it on a physical Android Device or emulator.

Or download it here on Google Play: _Currently being reviewed by Google before it will appear on Google Play._
