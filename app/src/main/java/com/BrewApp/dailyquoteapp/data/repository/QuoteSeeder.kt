package com.BrewApp.dailyquoteapp.data.repository

import android.util.Log
import com.BrewApp.dailyquoteapp.data.auth.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

object QuoteSeeder {

    @Serializable
    data class SeedQuote(
        val text: String,
        val author: String,
        val category: String
    )

    suspend fun seedDatabase() {
        val quotes = listOf(
            // --- MOTIVATION (20) ---
            SeedQuote("The only way to do great work is to love what you do.", "Steve Jobs", "Motivation"),
            SeedQuote("Believe you can and you're halfway there.", "Theodore Roosevelt", "Motivation"),
            SeedQuote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson", "Motivation"),
            SeedQuote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt", "Motivation"),
            SeedQuote("It does not matter how slowly you go as long as you do not stop.", "Confucius", "Motivation"),
            SeedQuote("Everything you've ever wanted is on the other side of fear.", "George Addair", "Motivation"),
            SeedQuote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill", "Motivation"),
            SeedQuote("Hardships often prepare ordinary people for an extraordinary destiny.", "C.S. Lewis", "Motivation"),
            SeedQuote("Believe in yourself. You are braver than you think, more talented than you know, and capable of more than you imagine.", "Roy T. Bennett", "Motivation"),
            SeedQuote("I learned that courage was not the absence of fear, but the triumph over it.", "Nelson Mandela", "Motivation"),
            SeedQuote("There is only one thing that makes a dream impossible to achieve: the fear of failure.", "Paulo Coelho", "Motivation"),
            SeedQuote("It’s not whether you get knocked down, it’s whether you get up.", "Vince Lombardi", "Motivation"),
            SeedQuote("If you are working on something that you really care about, you don’t have to be pushed. The vision pulls you.", "Steve Jobs", "Motivation"),
            SeedQuote("People who are crazy enough to think they can change the world, are the ones who do.", "Rob Siltanen", "Motivation"),
            SeedQuote("Failure will never overtake me if my determination to succeed is strong enough.", "Og Mandino", "Motivation"),
            SeedQuote("Entrepreneurs are great at dealing with uncertainty and also very good at minimizing risk.", "Mohnish Pabrai", "Motivation"),
            SeedQuote("We may encounter many defeats but we must not be defeated.", "Maya Angelou", "Motivation"),
            SeedQuote("Knowing is not enough; we must apply. Wishing is not enough; we must do.", "Johann Wolfgang Von Goethe", "Motivation"),
            SeedQuote("Imagine your life is perfect in every respect; what would it look like?", "Brian Tracy", "Motivation"),
            SeedQuote("Security is mostly a superstition. Life is either a daring adventure or nothing.", "Helen Keller", "Motivation"),

            // --- LOVE (20) ---
            SeedQuote("Love is composed of a single soul inhabiting two bodies.", "Aristotle", "Love"),
            SeedQuote("Being deeply loved by someone gives you strength, while loving someone deeply gives you courage.", "Lao Tzu", "Love"),
            SeedQuote("The best thing to hold onto in life is each other.", "Audrey Hepburn", "Love"),
            SeedQuote("To love and be loved is to feel the sun from both sides.", "David Viscott", "Love"),
            SeedQuote("Love is not about how many days, months, or years you have been together. Love is about how much you love each other every single day.", "Unknown", "Love"),
            SeedQuote("There is no charm equal to tenderness of heart.", "Jane Austen", "Love"),
            SeedQuote("Love involves a rare combination of understanding and misunderstanding.", "Diane Arbus", "Love"),
            SeedQuote("You know you're in love when you can't fall asleep because reality is finally better than your dreams.", "Dr. Seuss", "Love"),
            SeedQuote("Love recognizes no barriers. It jumps hurdles, leaps fences, penetrates walls to arrive at its destination full of hope.", "Maya Angelou", "Love"),
            SeedQuote("The greatest happiness of life is the conviction that we are loved.", "Victor Hugo", "Love"),
            SeedQuote("We are most alive when we are in love.", "John Updike", "Love"),
            SeedQuote("Love is a friendship set to music.", "Joseph Campbell", "Love"),
            SeedQuote("Love is when the other person's happiness is more important than your own.", "H. Jackson Brown, Jr.", "Love"),
            SeedQuote("If I know what love is, it is because of you.", "Hermann Hesse", "Love"),
            SeedQuote("Love looks not with the eyes, but with the mind.", "William Shakespeare", "Love"),
            SeedQuote("Nobody has ever measured, not even poets, how much the heart can hold.", "Zelda Fitzgerald", "Love"),
            SeedQuote("Where there is love there is life.", "Mahatma Gandhi", "Love"),
            SeedQuote("Love creates an 'us' without destroying a 'me'.", "Leo Buscaglia", "Love"),
            SeedQuote("Darkness cannot drive out darkness: only light can do that. Hate cannot drive out hate: only love can do that.", "Martin Luther King Jr.", "Love"),
            SeedQuote("At the touch of love everyone becomes a poet.", "Plato", "Love"),

            // --- SUCCESS (20) ---
            SeedQuote("Success usually comes to those who are too busy to be looking for it.", "Henry David Thoreau", "Success"),
            SeedQuote("Don't be afraid to give up the good to go for the great.", "John D. Rockefeller", "Success"),
            SeedQuote("I find that the harder I work, the more luck I seem to have.", "Thomas Jefferson", "Success"),
            SeedQuote("Success is walking from failure to failure with no loss of enthusiasm.", "Winston Churchill", "Success"),
            SeedQuote("The way to get started is to quit talking and begin doing.", "Walt Disney", "Success"),
            SeedQuote("If you really look closely, most overnight successes took a long time.", "Steve Jobs", "Success"),
            SeedQuote("The secret of success is to do the common thing uncommonly well.", "John D. Rockefeller Jr.", "Success"),
            SeedQuote("Success seems to be connected with action. Successful people keep moving.", "Conrad Hilton", "Success"),
            SeedQuote("There are no secrets to success. It is the result of preparation, hard work, and learning from failure.", "Colin Powell", "Success"),
            SeedQuote("Success is not the key to happiness. Happiness is the key to success.", "Albert Schweitzer", "Success"),
            SeedQuote("Success differs from luck. Luck is when you get something for nothing. Success is when you clear a path for yourself.", "Arnold Schwarzenegger", "Success"),
            SeedQuote("However difficult life may seem, there is always something you can do and succeed at.", "Stephen Hawking", "Success"),
            SeedQuote("Success is how high you bounce when you hit bottom.", "George S. Patton", "Success"),
            SeedQuote("A successful man is one who can lay a firm foundation with the bricks others have thrown at him.", "David Brinkley", "Success"),
            SeedQuote("The only place where success comes before work is in the dictionary.", "Vidal Sassoon", "Success"),
            SeedQuote("Stop chasing the money and start chasing the passion.", "Tony Hsieh", "Success"),
            SeedQuote("The road to success and the road to failure are almost exactly the same.", "Colin R. Davis", "Success"),
            SeedQuote("Success is getting what you want, happiness is wanting what you get.", "W.P. Kinsella", "Success"),
            SeedQuote("I never dreamed about success. I worked for it.", "Estée Lauder", "Success"),
            SeedQuote("Don't let the fear of losing be greater than the excitement of winning.", "Robert Kiyosaki", "Success"),

            // --- WISDOM (20) ---
            SeedQuote("The only true wisdom is in knowing you know nothing.", "Socrates", "Wisdom"),
            SeedQuote("In the end, it's not the years in your life that count. It's the life in your years.", "Abraham Lincoln", "Wisdom"),
            SeedQuote("Life is what happens when you're busy making other plans.", "John Lennon", "Wisdom"),
            SeedQuote("The journey of a thousand miles begins with one step.", "Lao Tzu", "Wisdom"),
            SeedQuote("It is better to remain silent at the risk of being thought a fool, than to talk and remove all doubt.", "Maurice Switzer", "Wisdom"),
            SeedQuote("The unexamined life is not worth living.", "Socrates", "Wisdom"),
            SeedQuote("Turn your wounds into wisdom.", "Oprah Winfrey", "Wisdom"),
            SeedQuote("The fool doth think he is wise, but the wise man knows himself to be a fool.", "William Shakespeare", "Wisdom"),
            SeedQuote("Count your age by friends, not years. Count your life by smiles, not tears.", "John Lennon", "Wisdom"),
            SeedQuote("Knowing others is intelligence; knowing yourself is true wisdom.", "Lao Tzu", "Wisdom"),
            SeedQuote("It is the mark of an educated mind to be able to entertain a thought without accepting it.", "Aristotle", "Wisdom"),
            SeedQuote("A wise man can learn more from a foolish question than a fool can learn from a wise answer.", "Bruce Lee", "Wisdom"),
            SeedQuote("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Aristotle", "Wisdom"),
            SeedQuote("Yesterday I was clever, so I wanted to change the world. Today I am wise, so I am changing myself.", "Rumi", "Wisdom"),
            SeedQuote("He who has a why to live can bear almost any how.", "Friedrich Nietzsche", "Wisdom"),
            SeedQuote("Everything that irritates us about others can lead us to an understanding of ourselves.", "Carl Jung", "Wisdom"),
            SeedQuote("The simple things are also the most extraordinary things, and only the wise can see them.", "Paulo Coelho", "Wisdom"),
            SeedQuote("Do not go where the path may lead, go instead where there is no path and leave a trail.", "Ralph Waldo Emerson", "Wisdom"),
            SeedQuote("Educating the mind without educating the heart is no education at all.", "Aristotle", "Wisdom"),
            SeedQuote("Wise men speak because they have something to say; fools because they have to say something.", "Plato", "Wisdom"),

            // --- HUMOR (20) ---
            SeedQuote("I am so clever that sometimes I don't understand a single word of what I am saying.", "Oscar Wilde", "Humor"),
            SeedQuote("Two things are infinite: the universe and human stupidity; and I'm not sure about the universe.", "Albert Einstein", "Humor"),
            SeedQuote("Always borrow money from a pessimist. He won't expect it back.", "Oscar Wilde", "Humor"),
            SeedQuote("My therapist told me the way to achieve true inner peace is to finish what I start. So far I’ve finished two bags of M&Ms.", "Dave Barry", "Humor"),
            SeedQuote("People say nothing is impossible, but I do nothing every day.", "A.A. Milne", "Humor"),
            SeedQuote("If you think you are too small to make a difference, try sleeping with a mosquito.", "Dalai Lama", "Humor"),
            SeedQuote("A day without sunshine is like, you know, night.", "Steve Martin", "Humor"),
            SeedQuote("My bed is a magical place where I suddenly remember everything I forgot to do.", "Unknown", "Humor"),
            SeedQuote("I'm on a seafood diet. I see food and I eat it.", "Unknown", "Humor"),
            SeedQuote("Common sense is like deodorant. The people who need it most never use it.", "Unknown", "Humor"),
            SeedQuote("I told my wife she was drawing her eyebrows too high. She looked surprised.", "Unknown", "Humor"),
            SeedQuote("I haven’t spoken to my wife in years. I didn’t want to interrupt her.", "Rodney Dangerfield", "Humor"),
            SeedQuote("I used to think I was indecisive, but now I am not so sure.", "Unknown", "Humor"),
            SeedQuote("Doing nothing is hard, you never know when you're done.", "Unknown", "Humor"),
            SeedQuote("Life is short. Smile while you still have teeth.", "Unknown", "Humor"),
            SeedQuote("Behind every great man is a woman rolling her eyes.", "Jim Carrey", "Humor"),
            SeedQuote("I walk around like everything’s fine, but deep down, inside my shoe, my sock is sliding off.", "Unknown", "Humor"),
            SeedQuote("I don’t suffer from insanity, I enjoy every minute of it.", "Unknown", "Humor"),
            SeedQuote("If you’re going to be thinking, you may as well think big.", "Donald Trump", "Humor"),
            SeedQuote("Between two evils, I always pick the one I never tried before.", "Mae West", "Humor")
        )

        try {
            // Insert all quotes
            SupabaseClient.client.from("quotes").insert(quotes)
            Log.d("QuoteSeeder", "Seeding successful: ${quotes.size} quotes inserted.")
        } catch (e: Exception) {
            Log.e("QuoteSeeder", "Seeding failed: ${e.message}")
        }
    }
}