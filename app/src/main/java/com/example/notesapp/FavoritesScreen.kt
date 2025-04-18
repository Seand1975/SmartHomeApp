package com.example.notesapp

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FavoritesScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Star Icon
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = "No Favorites Star",
                tint = Color.Gray,
                modifier = Modifier
                    .size(64.dp) // Larger size for the star to match the image
            )

            Spacer(modifier = Modifier.height(16.dp))

            // "No Favorites!" text
            Text(
                text = "No Favorites!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp // Slightly smaller than headlineMedium for better match
                ),
                textAlign = TextAlign.Center,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Instruction text
            Text(
                text = "Add your favorite routines for easy access here.\nTap the '+' button below to add your favorite routines.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center,
                color = Color.Black
            )
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { /* Handle click */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = highlightYellow,
            contentColor = Color.Black
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Favorite"
            )
        }
    }
}